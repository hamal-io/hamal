import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
    id("com.bmuschko.docker-spring-boot-application").version("9.3.1")
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("fnguru")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}


kotlin {
    jvmToolchain(19)
}


docker {
    springBootApplication {
        maintainer.set("hamal.io docker@hamal.io")
        baseImage.set("openjdk:22-slim-bullseye")
        ports.set(listOf(6006, 6006))
        images.set(listOf("hamalio/fn-guru"))
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
