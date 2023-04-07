plugins {
    id("hamal.kotlin-application-conventions")
    id("org.springframework.boot").version("3.0.5")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-notification"))

//    implementation(external.springWeb) {
//        exclude("com.fasterxml.jackson.core", "jackson-core")
//        exclude("org.springframework.boot", "spring-boot-starter-json")
//        exclude("com.fasterxml.jackson.core", "jackson-annotations")
//        exclude("org.springframework.boot", "spring-boot-starter-json")
//    }

//    implementation(external.springDataJdbc)
//    implementation(external.hikari)
//    implementation(external.h2)

    implementation(project(":module:launchpad:infra"))

    testImplementation(project(":application"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.springTest) {
        exclude("org.assertj", "*")
    }

    compileOnly(external.springDevTools)
}

application {
    // Define the main class for the application.
    mainClass.set("io.hamal.application.HamalApplication")
}
