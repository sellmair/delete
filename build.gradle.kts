import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

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
