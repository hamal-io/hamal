plugins {
    id("org.jetbrains.kotlin.jvm")
//    kotlin("plugin.serialization")
}

kotlin {
    jvmToolchain(19)
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
}

