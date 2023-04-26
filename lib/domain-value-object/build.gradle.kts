plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))
    implementation(project(":lib:util"))

    implementation(external.kotlin.protobuf)
    implementation(external.kotlin.reflect)

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
