plugins {
    id("hamal.kotlin-lib-conventions")
}

dependencies {
    implementation(project(":lib:meta"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}