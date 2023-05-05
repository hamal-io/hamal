import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":lib:core"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
        exclude("org.springframework.boot", "spring-boot-starter-json")
    }

    implementation("org.xerial:sqlite-jdbc:3.25.2")
    implementation(external.kotlin.json)


    implementation(project(":backend:infra"))
    implementation(project(":worker:infra"))

    testImplementation(project(":bootstrap"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.spring.test) {
        exclude("org.assertj", "*")
    }

    compileOnly(external.spring.devTools)
}

tasks.named<BootJar>("bootJar") {
    launchScript()
}