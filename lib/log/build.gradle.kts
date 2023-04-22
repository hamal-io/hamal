plugins {
    id("hamal.lib")
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:util"))
    implementation(external.sqlite)

    implementation(external.kotlin.cbor)

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(project(":lib:log"))
}