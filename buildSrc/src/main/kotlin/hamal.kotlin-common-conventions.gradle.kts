plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin{
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

val testIntegration by sourceSets.creating
configurations[testIntegration.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[testIntegration.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

tasks.test {
    useJUnitPlatform()
}

val testIntegrationTask = tasks.register<Test>("testIntegration") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = testIntegration.output.classesDirs
    classpath = configurations[testIntegration.runtimeClasspathConfigurationName] + testIntegration.output

    shouldRunAfter(tasks.test)

    useJUnitPlatform()
    testLogging {
        events("passed")
    }
}

tasks.check {
    dependsOn(testIntegrationTask)
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

