plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}

