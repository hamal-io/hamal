plugins {
    id("hamal.kotlin-application-conventions")
    id("org.springframework.boot").version("3.0.5")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":lib:meta"))
    implementation(project(":lib:ddd"))

//    implementation(external.springWeb) {
//        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
//        exclude(group = "org.springframework.boot", module = "spring-boot-starter-json")
//        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
//        exclude(group = "org.springframework.boot", module = "spring-boot-starter-json")
//    }

//    implementation(external.springWeb)
//    implementation(external.springDataJdbc)
//    implementation(external.hikari)
//    implementation(external.h2)

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.data:spring-data-jdbc")
    implementation( "com.h2database:h2")
    implementation( "com.zaxxer:HikariCP")

    testImplementation(project(":application"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.springTest)

    implementation("org.springframework.boot:spring-boot-devtools")
}

application {
    // Define the main class for the application.
    mainClass.set("io.hamal.application.HamalApplication")
}
