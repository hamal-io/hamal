import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
archivesName.set("script-api")

dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:domain"))

    testImplementation(project(":lib:script:api"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}