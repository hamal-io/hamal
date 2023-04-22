plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
