plugins {
	id("fabric-loom") version "1.7-SNAPSHOT"
	id("maven-publish")
}

version = property("mod_version")!!
group = property("maven_group")!!

base {
	archivesName.set(property("archives_base_name") as String)
}

repositories {
	maven("https://maven.isxander.dev/releases")
	maven("https://maven.terraformersmc.com/")
	exclusiveContent {
		forRepository {
			maven {
				url = uri("https://maven.azureaaron.net/releases")
			}

		}
		filter {
			includeGroup("net.azureaaron")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation ("net.fabricmc:fabric-loader:${property("loader_version")}")
	modImplementation ("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
	modImplementation ("dev.isxander:yet-another-config-lib:${property("yacl_version")}")
	modImplementation ("com.terraformersmc:modmenu:${property("modmenu_version")}")
	include(modImplementation("net.azureaaron:hm-api:${property("hmapi_version")}") as Any)

}
tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(21)
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<Jar>("jar") {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}" }
	}
}
