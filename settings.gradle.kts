pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.4"
	id("dev.kikugie.loom-back-compat") version "0.3"
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
	create(rootProject) {
		// versions() 用于版本名 = MC版本字符串的情况
		// version(name, mcVersion) 用于版本名与实际MC版本不同的情况
		versions("1.20.1", "1.21.11")
		version("26.1", "26.1.2")
		// vcsVersion 指定 VCS 中当前实际开发的版本（即 src/ 目录对应的版本）
		vcsVersion = "1.21.11"
	}
}

rootProject.name = "carpet-as-addition"
