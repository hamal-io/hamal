plugins {
    id("hamal.lib")
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