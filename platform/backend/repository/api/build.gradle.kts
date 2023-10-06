import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("hub-repository-api")

dependencies {
    api(project(":platform:lib:domain"))
    testImplementation(external.hamcrest)
    testImplementation(external.junit)
}