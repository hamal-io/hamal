plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(project(":platform:lib:common"))
    api(project(":platform:lib:kua"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}