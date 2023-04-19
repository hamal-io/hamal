import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.kotlin-lib-conventions")
}
archivesName.set("script-api")

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:util"))

    testImplementation(project(":script:api"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}