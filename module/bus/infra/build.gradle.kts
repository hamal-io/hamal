import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.kotlin-module-conventions")
    id("org.springframework.boot").version("3.0.5")
}
archivesName.set("bus-infra")

apply(plugin = "io.spring.dependency-management")


dependencies {
    implementation(project(":module:bus:api"))

    implementation(external.springWeb) {
//        exclude("com.fasterxml.jackson.core", "jackson-core")
//        exclude("org.springframework.boot", "spring-boot-starter-json")
//        exclude("com.fasterxml.jackson.core", "jackson-annotations")
//        exclude("org.springframework.boot", "spring-boot-starter-json")
    }

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
//    testImplementation(external.springTest) {
//        exclude("org.assertj", "*")
//    }

}

tasks.bootJar {
    enabled = false
}