import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
archivesName.set("repository-sqlite")

dependencies {
    implementation(project(":repository:api"))
}