import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.backend")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}
archivesName.set("backend-infra")

apply(plugin = "io.spring.dependency-management")


dependencies {
    implementation(project(":lib:common"))
    implementation(project(":lib:domain"))

    implementation(project(":backend:usecase"))
    implementation(project(":backend:core"))
    implementation(project(":backend:repository:api"))
    implementation(project(":backend:repository:memory"))
    implementation(project(":backend:repository:sqlite"))

    implementation(external.kotlin.json)
    implementation(external.kotlin.protobuf)

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
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