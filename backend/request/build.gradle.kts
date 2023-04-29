import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
}
archivesName.set("backend-application")

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":backend:core"))

    implementation(project(":backend:store:api"))
    implementation(project(":backend:store:impl")) // FIXME get rid of this

    testImplementation(project(":backend:request"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}