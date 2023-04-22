plugins {
    id("hamal.lib")
}

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:meta"))
    implementation(project(":lib:domain-value-object"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
}
