plugins {
    id("hamal.kotlin-lib-conventions")
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}