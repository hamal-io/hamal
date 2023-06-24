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
            testType.set(TestSuiteType.UNIT_TEST)

            targets {
                all {
                    testTask.configure {
                        systemProperties(
                            mapOf(
                                "junit.jupiter.execution.parallel.enabled" to true,
                                "junit.jupiter.execution.parallel.mode.default" to "same_thread",
                                "junit.jupiter.execution.parallel.mode.classes.default" to "concurrent",
                            )
                        )
                    }
                }
            }
        }

        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.INTEGRATION_TEST)

            sources {
                kotlin {
                    setSrcDirs(listOf("src/integrationTest/kotlin"))
                }
            }

            targets {
                all {
                    testTask.configure {
                        systemProperties(
                            mapOf(
                                "junit.jupiter.execution.parallel.enabled" to true,
                                "junit.jupiter.execution.parallel.mode.default" to "same_thread",
                                "junit.jupiter.execution.parallel.mode.classes.default" to "concurrent",
                            )
                        )
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

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
    dependsOn(tasks.check)
}
