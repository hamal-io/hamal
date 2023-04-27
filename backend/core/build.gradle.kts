import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-core")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-value-object"))
    implementation(project(":lib:util"))

    implementation(external.kotlin.json)
    implementation(external.kotlin.protobuf)
}