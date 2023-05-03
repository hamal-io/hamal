import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-impl")

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":backend:core"))
    implementation(project(":backend:repository:api"))

    implementation(external.sqlite)
    implementation(external.spring.logging)
    implementation(external.kotlin.protobuf)

    testImplementation(project(":backend:repository:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

