import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
archivesName.set("backend-application")

dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:domain"))
    implementation(project(":backend:core"))

    implementation(project(":backend:repository:api"))

    testImplementation(project(":backend:usecase"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}