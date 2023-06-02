import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "backend"
archivesName.set("backend-impl")

dependencies {
    implementation(project(":lib:sdk"))
    implementation(project(":lib:script:impl"))

    implementation(project(":backend:repository:api"))
    implementation(project(":backend:repository:memory"))
    implementation(project(":backend:repository:sqlite"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":agent:extension:api"))
    implementation(project(":agent:extension:std:log"))

    testImplementation(project(":backend:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.spring.test) {
        exclude("org.assertj", "*")
    }
}