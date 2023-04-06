plugins {
    id("hamal.kotlin-application-conventions")
    id("org.springframework.boot").version("3.0.5")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))

    implementation(external.springWeb) {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-json")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-json")
    }

    implementation(external.springWeb)
    implementation(external.springDataJdbc)
    implementation(external.hikari)
    implementation(external.h2)

    testImplementation(project(":application"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.springTest)

    compileOnly(external.springDevTools)
}

application {
    // Define the main class for the application.
    mainClass.set("io.hamal.application.HamalApplication")
}
