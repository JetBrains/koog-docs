plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlinx.knit") version "0.5.0"
    kotlin("plugin.serialization") version "2.1.20"
}

repositories {
    mavenCentral()
}

val koogVersion = "0.3.0"
val logBackVersion = "1.5.13"

dependencies {
    implementation("ai.koog:koog-agents:$koogVersion")
    implementation("ai.koog:agents-test:$koogVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("ch.qos.logback:logback-classic:$logBackVersion")
    implementation("io.opentelemetry:opentelemetry-exporter-logging")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
}

knit {
    rootDir = project.rootDir
    files = fileTree("docs/") {
        include("**/*.md")
    }
    moduleDocs = "docs/modules.md"
    siteRoot = "https://docs.koog.ai/"
}
