plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    java
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


kotlin {
    jvmToolchain(21)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
}

