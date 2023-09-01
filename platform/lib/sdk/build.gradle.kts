plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}


dependencies {
    api(project(":platform:lib:http"))
    api(project(":platform:lib:domain"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
