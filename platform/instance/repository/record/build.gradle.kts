import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("instance-repository-record")

dependencies {
    api(project(":platform:instance:repository:api"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
