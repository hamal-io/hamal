import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.kotlin-lib-conventions")
}
archivesName.set("bus-producer")

dependencies {
    implementation(project(":module:bus:api"))
    implementation(project(":module:bus:impl:core"))
}