plugins {
    id("hamal.lib")
    kotlin("plugin.serialization") version "1.8.10"
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:util"))
    implementation(external.sqlite)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(project(":lib:log"))
}