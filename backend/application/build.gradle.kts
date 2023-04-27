import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
}
archivesName.set("backend-application")

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":backend:core"))

    testImplementation(project(":backend:application"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}