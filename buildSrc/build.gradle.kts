plugins {
    `kotlin-dsl`
    kotlin("jvm").version("1.8.10")
    kotlin("plugin.serialization").version("1.8.10")
}

kotlin{
    jvmToolchain(19)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}
