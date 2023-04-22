plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))

    implementation(external.kotlin.protobuf)

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
