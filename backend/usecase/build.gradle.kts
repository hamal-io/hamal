import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
archivesName.set("backend-application")

dependencies {
    api(project(":backend:core"))
    api(project(":backend:repository:api"))

    testImplementation(project(":backend:usecase"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}