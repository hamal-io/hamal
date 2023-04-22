import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.lib")
}
archivesName.set("launchpad-core")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-value-object"))
}