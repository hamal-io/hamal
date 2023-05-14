plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")

}

apply(plugin = "io.spring.dependency-management")


dependencies {
    api(project(":lib:http"))

    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation(external.kotlin.json)

    testImplementation(project(":lib:web3"))
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