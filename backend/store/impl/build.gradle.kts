import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-api")

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":backend:core"))
    implementation(project(":backend:store:api"))

    implementation(external.spring.jdbc)
    implementation(external.sqlite)
}

