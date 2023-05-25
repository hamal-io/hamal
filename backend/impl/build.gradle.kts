import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}
archivesName.set("backend-impl")

apply(plugin = "io.spring.dependency-management")


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

    testImplementation(project(":backend:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.spring.test) {
        exclude("org.assertj", "*")
    }
    compileOnly(external.spring.devTools)
}

tasks.bootJar {
    enabled = false
}