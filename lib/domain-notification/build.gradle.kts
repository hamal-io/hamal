plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:meta"))
    implementation(project(":lib:domain-value-object"))
    implementation(external.kotlin.reflect)
    implementation(external.kotlin.cbor)
}
