plugins {
    id("hamal.common")
    application

    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))
    implementation(project(":lib:log"))
    implementation(project(":lib:domain-notification"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
        exclude("org.springframework.boot", "spring-boot-starter-json")
    }

    implementation("org.xerial:sqlite-jdbc:3.25.2")

    implementation(external.kotlin.cbor)
//    implementation(external.springDataJdbc)
//    implementation(external.hikari)
//    implementation(external.h2)

    implementation(project(":module:launchpad:infra"))
    implementation(project(":module:queue:infra"))
    implementation(project(":module:worker:infra"))

    testImplementation(project(":application"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.spring.test) {
        exclude("org.assertj", "*")
    }

    compileOnly(external.spring.devTools)
}
