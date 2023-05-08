plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation(external.kotlin.protobuf)
    implementation(external.kotlin.json)
    implementation(external.apache.http.client)
    implementation(external.apache.http.mime)
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}