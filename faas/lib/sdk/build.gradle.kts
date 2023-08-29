plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}


dependencies {
    api(project(":faas:lib:http"))
    api(project(":faas:lib:domain"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
