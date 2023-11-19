import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
    id("com.bmuschko.docker-spring-boot-application") version "9.3.1"
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("hamal-backend")

docker {
    springBootApplication {
        maintainer.set("hamal.io docker@hamal.io")
        baseImage.set("openjdk:22-slim-bullseye")
        ports.set(
            listOf(
                7007, 7007,
                8008, 8008
            )
        )
        images.set(listOf("hamalio/hamal-backend"))
        jvmArgs.set(listOf("-Xmx1024m", "-XX:+ExitOnOutOfMemoryError"))
    }
}

dependencies {
    implementation(project(":platform:lib:sdk"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":platform:admin"))
    implementation(project(":platform:backend:api"))
    implementation(project(":platform:backend:bridge"))
    implementation(project(":platform:backend:core"))
    implementation(project(":platform:runner"))
}

tasks.named<BootJar>("bootJar") {
    launchScript()
}