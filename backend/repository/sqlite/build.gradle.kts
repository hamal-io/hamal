import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-sqlite")

dependencies {
    api(project(":backend:repository:api"))

    implementation(external.sqlite)

    testImplementation(project(":backend:repository:sqlite"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

