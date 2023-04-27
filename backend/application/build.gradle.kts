import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
}
archivesName.set("backend-application")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-value-object"))
    implementation(project(":lib:util"))
    implementation(project(":backend:core"))
}