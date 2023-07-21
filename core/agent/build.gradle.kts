import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("agent-impl")

dependencies {
    implementation(project(":lib:sdk"))
    implementation(project(":lib:web3"))
    implementation(project(":lib:kua"))

    implementation(project(":extension:api"))
    implementation(project(":extension:std:sys"))
    implementation(project(":extension:std:log"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}