import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.lib")
}
archivesName.set("script-impl")

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":script:api"))
    implementation(external.spring.logging)

    testImplementation(project(":script:api"))
    testImplementation(project(":script:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}