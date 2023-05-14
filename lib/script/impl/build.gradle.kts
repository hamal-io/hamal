import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
archivesName.set("script-impl")

dependencies {
    api(project(":lib:domain"))
    api(project(":lib:script:api"))
    implementation(external.spring.logging)

    testImplementation(project(":lib:script:api"))
    testImplementation(project(":lib:script:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}