plugins {
    id("hamal.lib")
}

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:meta"))
    implementation(project(":lib:domain-value-object"))
    implementation(external.kotlin.reflect)
    implementation(external.kotlin.cbor)
}
