import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("hub-repository-record")

dependencies {
    api(project(":platform:backend:repository:api"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
