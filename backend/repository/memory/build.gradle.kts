import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-memory")

dependencies {
    api(project(":backend:repository:api"))

    testImplementation(project(":backend:repository:memory"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

