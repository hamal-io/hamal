plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:common"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation(external.kotlin.protobuf)
    implementation(external.kotlin.json)
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}