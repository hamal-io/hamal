plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:meta"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}