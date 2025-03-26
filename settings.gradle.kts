pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.kikugie.dev/snapshots")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.6-alpha.13"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	create(rootProject) {
		versions("1.21.1", "1.21.4", "1.21.5")
		vcsVersion = "1.21.4"
	}
}
