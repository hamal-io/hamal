import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.kotlin-lib-conventions")
}
archivesName.set("script-impl")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:meta"))
    implementation(project(":lib:util"))
    implementation(project(":script:api"))

    testImplementation(project(":script:api"))
    testImplementation(project(":script:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}