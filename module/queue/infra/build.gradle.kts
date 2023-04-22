import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.module")
    id("org.springframework.boot").version("3.0.5")
}
archivesName.set("queue-infra")

apply(plugin = "io.spring.dependency-management")


dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-notification"))
    implementation(project(":lib:domain-value-object"))
    implementation(project(":lib:util"))
    implementation(project(":module:queue:application"))
    implementation(project(":module:queue:core"))

    implementation(external.spring.web) {
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