import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("script-api")

dependencies {
    implementation(project(":lib:common"))

    testImplementation(project(":lib:script:api"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}