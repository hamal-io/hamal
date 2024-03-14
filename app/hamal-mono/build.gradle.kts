import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.springframework.boot").version("3.2.3")
    kotlin("plugin.spring").version("1.9.23")
    id("com.bmuschko.docker-spring-boot-application").version("9.3.6")
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("hamal-mono")

docker {
    springBootApplication {
        maintainer.set("hamal.io docker@hamal.io")
        baseImage.set("openjdk:22-slim-bullseye")
        ports.set(
            listOf(
                8008, 8008,
                9009, 9009
            )
        )
        images.set(listOf("hamalio/hamal-mono"))
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

    implementation(project(":platform:backend:api"))
    implementation(project(":platform:backend:bridge"))
    implementation(project(":platform:backend:core"))
    implementation(project(":platform:runner:runner"))
}

tasks.named<BootJar>("bootJar") {
    launchScript()
}
