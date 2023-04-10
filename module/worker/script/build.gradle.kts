import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.kotlin-lib-conventions")
}
archivesName.set("worker-script")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:meta"))
    implementation(project(":lib:util"))

    testImplementation(project(":module:worker:script"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}