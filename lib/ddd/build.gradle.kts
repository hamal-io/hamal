plugins {
    id("hamal.lib")
}

dependencies {
    implementation(project(":lib:meta"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}