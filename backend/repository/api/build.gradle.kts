import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
archivesName.set("backend-repository-api")

dependencies {
    api(project(":backend:core"))

    testImplementation(external.hamcrest)
    testImplementation(external.junit)
}