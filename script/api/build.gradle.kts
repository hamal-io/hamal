import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.lib")
}
archivesName.set("script-api")

dependencies {
    implementation(project(":lib"))

    testImplementation(project(":script:api"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}