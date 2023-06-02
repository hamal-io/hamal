plugins {
    `java-library`
    `jvm-test-suite`
    id("org.jetbrains.kotlin.jvm")
    id("jacoco")
    id("jacoco-report-aggregation")
}


testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val integrationTest by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/integrationTest/kotlin"))
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
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

tasks.register<JacocoReport>("testIntegrationCodeCoverageReport") {
    executionData(layout.buildDirectory.file("jacoco/testIntegration.exec").get().asFile)
    sourceSets(testIntegration)
    sourceSets(sourceSets.main.get())
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
    dependsOn(tasks.check)
}
