plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}


dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:domain"))
    implementation(project(":lib:http"))

    implementation(external.kotlin.json)

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
