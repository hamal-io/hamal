import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    application
    id("org.springframework.boot").version("3.2.3")
    kotlin("plugin.spring").version("1.9.23")
    id("com.bmuschko.docker-spring-boot-application").version("9.3.6")
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("candyrocks")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


kotlin {
    jvmToolchain(21)
}


docker {
    springBootApplication {
        maintainer.set("hamal.io docker@hamal.io")
        baseImage.set("openjdk:22-slim-bullseye")
        ports.set(listOf(5005, 5005))
        images.set(listOf("hamalio/candy-rocks"))
        jvmArgs.set(listOf("-Xmx512m", "-XX:+ExitOnOutOfMemoryError"))
    }
}



dependencies {
    implementation(project(":platform:lib:common"))
    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }
}
