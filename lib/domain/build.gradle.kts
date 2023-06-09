plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(project(":lib:common"))
    api(project(":lib:script:api"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}