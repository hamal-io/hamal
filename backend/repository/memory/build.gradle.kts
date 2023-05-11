import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-memory")

dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:domain"))
    implementation(project(":backend:core"))
    implementation(project(":backend:repository:api"))

    implementation(external.spring.logging)
    implementation(external.kotlin.protobuf)

    testImplementation(project(":backend:repository:memory"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

