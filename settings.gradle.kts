pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
  }
}

plugins { id("dev.kikugie.stonecutter") version "0.7-beta.6" }

stonecutter {
  create(rootProject) {
    versions("1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8")
    vcsVersion = "1.21.8"
  }
}

rootProject.name = "ldt"
