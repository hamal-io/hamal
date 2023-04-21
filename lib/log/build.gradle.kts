plugins {
    id("hamal.kotlin-lib-conventions")
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:util"))
    implementation(external.sqlite)

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(project(":lib:log"))
}