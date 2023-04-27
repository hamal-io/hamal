import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}
archivesName.set("backend-infra")

apply(plugin = "io.spring.dependency-management")


dependencies {
    implementation(project(":lib:core"))
    implementation(project(":lib:log"))

    implementation(project(":backend:application"))
    implementation(project(":backend:core"))

    implementation(external.kotlin.json)

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
        exclude("org.springframework.boot", "spring-boot-starter-json")
    }

    testImplementation(project(":backend:infra"))
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