plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("jacoco")
    id("jacoco-report-aggregation")
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
}

tasks.check {
    dependsOn(testIntegrationTask)
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
}
