import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
archivesName.set("repository-api")

dependencies {
    implementation(project(":lib:domain"))
    implementation(project(":lib:domain-value-object"))
}