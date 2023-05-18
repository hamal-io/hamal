import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
   id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("agent-infra")

dependencies {
    implementation(project(":lib:sdk"))
    implementation(project(":lib:web3"))
    implementation(project(":lib:script:impl"))

    implementation(project(":agent:extension:api"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

tasks.bootJar {
    enabled = false
}