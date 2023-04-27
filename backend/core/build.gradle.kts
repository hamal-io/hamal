import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-core")

dependencies {
    implementation(project(":lib:core"))
    implementation(external.kotlin.json)
    implementation(external.kotlin.protobuf)

    testImplementation(project(":backend:core"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}