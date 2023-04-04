plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
//        implementation("org.apache.commons:commons-text:1.9")
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
