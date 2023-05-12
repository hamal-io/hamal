import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.module")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("worker-infra")

dependencies {
    implementation(project(":worker:application"))
    implementation(project(":worker:core"))
    implementation(project(":lib:common"))
    implementation(project(":lib:sdk"))

    implementation(project(":worker:extension:api"))
    implementation(external.kotlin.json)
    implementation(external.kotlin.protobuf)

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
//        exclude("org.springframework.boot", "spring-boot-starter-json")
    }

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

tasks.bootJar {
    enabled = false
}