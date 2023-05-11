plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(external.kotlin.reflect)
    implementation(external.kotlin.protobuf)
    implementation(external.kotlin.json)
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}