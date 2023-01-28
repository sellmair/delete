@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/") {
            mavenContent {
                includeGroupByRegex("org.jetbrains.kotlin.*")
            }
        }
        mavenCentral()
    }
    plugins {
        kotlin("multiplatform") version "1.8.20-Beta-48"
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/") {
            mavenContent {
                includeGroup("org.jetbrains.kotlin")
                includeGroup("org.jetbrains")
            }
        }
        mavenCentral()
    }
}
