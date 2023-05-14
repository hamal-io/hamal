plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(external.kotlin.reflect)
    api(external.kotlin.protobuf)
    api(external.kotlin.json)
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}