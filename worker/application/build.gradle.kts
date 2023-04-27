import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.module")
}
archivesName.set("worker-application")


dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-value-object"))

    implementation(project(":worker:core"))
}