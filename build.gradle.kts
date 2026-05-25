plugins {
	// loom-back-compat 根据 MC 版本自动选择正确的 Loom 变体：
	//   1.x 版本 → Intermediary 映射（标准 Fabric Loom）
	//   26.x 版本 → Official Mojang 映射
	id("dev.kikugie.loom-back-compat")
	id("maven-publish")
}

// 构建产物版本格式：mod版本+mc版本，例如 1.0.0+1.21.11
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
	sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
	sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
	else -> JavaVersion.VERSION_17
}

// 在项目级别捕获所有 Stonecutter 属性，避免在 task 块内的歧义
val modId = property("mod.id") as String
val modName = property("mod.name") as String
val modVersion = property("mod.version") as String
val modMcCompat = property("mod.mc_compat") as String
val modJavaMin = property("mod.java_min") as String
val fabricLoader = property("deps.fabric_loader") as String
val fabricApi = property("deps.fabric_api") as String
val carpetVersion = findProperty("deps.carpet") as String?

repositories {
	exclusiveContent {
		forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
		filter { includeGroup("maven.modrinth") }
	}
	// 26.1+ Carpet 在 masa Maven 发布（格式为 carpet:fabric-carpet:version）
	maven("https://masa.dy.fi/maven") { name = "masa Maven" }
}

loom {
	splitEnvironmentSourceSets()

	mods.create(modId).apply {
		sourceSet(sourceSets["main"])
		sourceSet(sourceSets["client"])
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	// loomx 自动根据 MC 版本选择正确映射（Intermediary 或 Official Mojang）
	loomx.applyMojangMappings()

	modImplementation("net.fabricmc:fabric-loader:$fabricLoader")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApi")

	// 1.20.1 / 1.21.x：Carpet 在 Modrinth 发布（版本号仅含 carpet 自身版本）
	// 26.1+：Carpet 在 masa Maven 发布（格式为 carpet:fabric-carpet:26.1+vDate）
	if (carpetVersion != null) {
		if (sc.current.parsed >= "26.1") {
			modImplementation("carpet:fabric-carpet:$carpetVersion")
		} else {
			modImplementation("maven.modrinth:carpet:$carpetVersion")
		}
	}

	// jspecify 注解库：MC 1.21+ 已内置，较旧版本需显式引入（仅编译期）
	compileOnly("org.jspecify:jspecify:1.0.0")
}

val mixinJava = "JAVA_${requiredJava.majorVersion}"

tasks.withType<ProcessResources>().configureEach {
	val props = mapOf(
		"id"        to modId,
		"name"      to modName,
		"version"   to modVersion,
		"minecraft" to modMcCompat,
		"java_min"  to modJavaMin,
	)
	inputs.properties(props)
	inputs.property("java", mixinJava)
	filesMatching("fabric.mod.json") { expand(props) }
	filesMatching("*.mixins.json") { filter { line -> line.replace("\"JAVA_21\"", "\"$mixinJava\"") } }
}

tasks.withType<JavaCompile>().configureEach {
	options.release = requiredJava.majorVersion.toInt()
}

java {
	withSourcesJar()
	sourceCompatibility = requiredJava
	targetCompatibility = requiredJava

	toolchain {
		vendor = JvmVendorSpec.ADOPTIUM
		languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
	}
}

// buildAndCollect：将所有版本的构建产物集中到 build/libs/<mod版本>/ 目录
tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
	into(rootProject.layout.buildDirectory.file("libs/$modVersion"))
	dependsOn("build")
}

tasks.named<Jar>("jar") {
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}
}
