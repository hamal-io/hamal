import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}

archivesName.set("platform-repository-api")

dependencies {
    api(project(":platform:lib:domain"))
    testImplementation(external.hamcrest)
    testImplementation(external.junit)
}