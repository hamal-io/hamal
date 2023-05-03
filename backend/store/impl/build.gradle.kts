import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-impl")

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":backend:core"))
    implementation(project(":backend:store:api"))

    implementation(external.sqlite)

    testImplementation(project(":backend:store:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

