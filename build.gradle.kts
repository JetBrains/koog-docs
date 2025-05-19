plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlinx.knit") version "0.5.0"
    kotlin("plugin.serialization") version "2.1.20"
}

repositories {
    mavenCentral()
    maven(url = "https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    maven(url = "https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
}

val koogVersion = "0.1.0-alpha.5+0.4.49"
val grazieVersion = "0.4.43"
val logBackVersion = "1.5.13"

dependencies {
    implementation("ai.koog.agents:agents-core:$koogVersion")
    implementation("ai.koog.agents:agents-ext:$koogVersion")
    implementation("ai.koog.prompt:prompt-executor-llms-all:$koogVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("ch.qos.logback:logback-classic:$logBackVersion")
    implementation("ai.grazie.client:client-ktor:${grazieVersion}")
}

knit {
    rootDir = project.rootDir
    files = fileTree("docs/") {
        include("**/*.md")
    }
    siteRoot = "https://koan-agents.labs.jb.gg"
}
