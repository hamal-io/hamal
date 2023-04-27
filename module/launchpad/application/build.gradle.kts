import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.lib")
}
archivesName.set("launchpad-application")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-notification"))
    implementation(project(":lib:domain-value-object"))
    implementation(project(":lib:util"))
    implementation(project(":module:launchpad:core"))
}