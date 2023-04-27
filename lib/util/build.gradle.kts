plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:meta"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")

    implementation(external.kotlin.protobuf)
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}