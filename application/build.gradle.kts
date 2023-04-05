plugins {
    id("hamal.kotlin-application-conventions")
}

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))

    implementation(external.springWeb) {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-json")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-json")
    }

    testImplementation(project(":application"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.springTest)
}

application {
    // Define the main class for the application.
    mainClass.set("io.hamal.application.HamalApplication")
}
