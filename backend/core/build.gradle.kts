import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-core")

dependencies {
    api(project(":lib:domain"))
    api(external.spring.logging)

    testImplementation(project(":backend:core"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}