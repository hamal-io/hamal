plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    java
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}


kotlin {
    jvmToolchain(19)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}

