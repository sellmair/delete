import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
}

kotlin {
    linuxX64()
    macosX64()
    macosArm64()

    targets.withType<KotlinNativeTarget>().all {
        binaries.executable {
            entryPoint = "io.sellmair.delete.main"
        }
    }

    tasks.withType<KotlinNativeTest>().all {
        workingDir = projectDir.absolutePath
    }

    sourceSets.all {
        languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    sourceSets.commonMain.get().dependencies {
        implementation("com.squareup.okio:okio:3.3.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    }

    sourceSets.commonTest.get().dependencies {
        implementation("com.squareup.okio:okio-fakefilesystem:3.3.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    }
}

tasks.register("install") {
    /* Only supports Mac hosts right now */
    if (!HostManager.hostIsMac) error("install task only supports macos hosts")

    val kotlinTargetForHost = kotlin.targets.withType<KotlinNativeTarget>()
        .find { it.konanTarget == HostManager.host } ?: error("Unsupported host: ${HostManager.host}")

    val executable = kotlinTargetForHost.binaries.getExecutable(NativeBuildType.RELEASE)
    val inputFile = executable.linkTaskProvider.flatMap { it.outputFile }

    dependsOn(executable.linkTaskProvider)
    inputs.file(inputFile)

    val outputFile = File("/usr/local/bin/del")
    outputs.file(outputFile)

    doLast {
        exec {
            commandLine("sudo", "-S", "cp", inputFile.get().absolutePath, outputFile.absolutePath)
        }
    }
}
