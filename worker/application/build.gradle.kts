import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
   id("hamal.common")
}
archivesName.set("worker-application")


dependencies {
    implementation(project(":worker:core"))
}