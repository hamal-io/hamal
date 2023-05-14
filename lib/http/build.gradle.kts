plugins {
    id("hamal.lib")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    api(project(":lib:common"))
    implementation(external.kotlin.protobuf)
    implementation(external.kotlin.json)
    implementation(external.apache.http.client)
    implementation(external.apache.http.mime)
    implementation(external.apache.commons.logging)

    testImplementation(project(":lib:http"))
    testImplementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }
    testImplementation(external.spring.test) {
        exclude("org.assertj", "*")
    }
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

tasks.bootJar {
    enabled = false
}