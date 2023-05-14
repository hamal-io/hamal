plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}


dependencies {
    api(project(":lib:http"))
    api(project(":lib:domain"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
