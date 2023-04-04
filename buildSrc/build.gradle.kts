plugins {
    `kotlin-dsl`
}

kotlin{
    jvmToolchain(17)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}
