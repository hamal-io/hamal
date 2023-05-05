import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
}
archivesName.set("backend-application")

dependencies {
    implementation(project(":lib"))
    implementation(project(":backend:core"))

    implementation(project(":backend:repository:api"))
    implementation(project(":backend:repository:impl")) // FIXME get rid of this

    testImplementation(project(":backend:usecase"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}