import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.module")
}
archivesName.set("queue-application")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain"))
    implementation(project(":lib:domain-notification"))
    implementation(project(":lib:domain-value-object"))
    implementation(project(":module:queue:core"))
}