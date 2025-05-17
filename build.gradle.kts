import com.google.gson.Gson
import java.io.FileOutputStream
import java.net.URI
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel

plugins {
	id("fabric-loom") version "1.10-SNAPSHOT"
}

version = property("mod_version")!! as String + "+mc" + property("minecraft_version")!!
group = property("maven_group")!!

base {
	archivesName.set(property("archives_base_name") as String)
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/skyblocktweaks.accesswidener")
}


repositories {
	maven("https://maven.isxander.dev/releases")
	maven("https://maven.terraformersmc.com/")
	repositories {
		exclusiveContent {
			forRepository {
				maven {
					name = "Modrinth"
					url = uri("https://api.modrinth.com/maven")
				}
			}
			filter {
				includeGroup("maven.modrinth")
			}
		}
	}
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
	maven("https://repo.hypixel.net/repository/Hypixel/")
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation ("net.fabricmc:fabric-loader:${property("loader_version")}")
	modImplementation ("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
	modImplementation ("dev.isxander:yet-another-config-lib:${property("yacl_version")}")
	modImplementation ("com.terraformersmc:modmenu:${property("modmenu_version")}")
	//include(modImplementation("net.azureaaron:hm-api:${property("hmapi_version")}") as Any)
	implementation("net.hypixel:mod-api:${property("modapi_version")}")
	include(modImplementation("maven.modrinth:hypixel-mod-api:${property("modapi_fabric_version")}") as Any)
	modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.1") 


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
	manifest.attributes.run {
		this["Main-Class"] = "SkyblockTweaksInstallerFrame"
	}
}





object RepoDownloader {

	private data class Branch(val name: String, val commit: Commit)
	private data class Commit(val sha: String, val url: String)


	private const val user = "SkyblockTweaks"
	private const val repo = "data"
	private const val branch = "main"

	fun downloadRepoZip (fallbackFolder: File) {
		val url: String = "https://api.github.com/repos/$user/$repo/zipball/$branch"
		downloadFile(url, fallbackFolder.resolve("repo.zip"))
		val sha = getLatestSha()
		val fallbackManifest = fallbackFolder.resolve("manifest.json")
		fallbackManifest.writeText(
			"""
			{
				"generatedAt": ${System.currentTimeMillis()},
				"repo": "$user/$repo#$branch",
				"commit": "$sha"
			}
			""".trimIndent())

    }

	fun getLatestSha(): String {
		val url = "https://api.github.com/repos/$user/$repo/branches/$branch"
		val connection = URI(url).toURL().openConnection().apply {
			setRequestProperty("User-Agent", "Mozilla/5.0 SkyblockTweaks Build Pipeline")
		}


		val response = connection.getInputStream().bufferedReader().readText()
		val branch = Gson().fromJson(response, Branch::class.java)
		return branch.commit.sha
	}

	private fun downloadFile(uri: String, destination: File) {
		val connection =  URI(uri).toURL().openConnection().apply {
			setRequestProperty("User-Agent", "Mozilla/5.0 SkyblockTweaks Build Pipeline")
		}
		val channel: ReadableByteChannel = Channels.newChannel(connection.getInputStream())
		FileOutputStream(destination).use {
			it.channel.transferFrom(channel, 0, Long.MAX_VALUE)
		}
	}

}

/**
 * This task updates the fallback repo zip file in the resources folder to the latest version.
 */
tasks.register("updateFallbackRepo") {
	group = "sbt"
	doLast {
		val fallbackFolder = rootDir.resolve("src/main/resources/fallback")
		fallbackFolder.mkdir()
		RepoDownloader.downloadRepoZip(fallbackFolder)
	}
}

tasks.register("validateJson") {
	group = "sbt"
	doLast {
		val resources = File(rootDir, "src/main/resources")
		val files = resources.walkTopDown()
			.filter { it.extension == "json" }
			.toList()
		for (file in files) {
			try {
				val content = file.readText()
				Gson().fromJson(content, Any::class.java)
			} catch (e: Exception) {
				println("Invalid JSON in file: ${file.absolutePath}")
				println("Error: ${e.message}")
			}
		}
	}
}

tasks.named("processResources") {
	mustRunAfter("validateJson")
}

tasks.named("build") {
	dependsOn("validateJson")
}

