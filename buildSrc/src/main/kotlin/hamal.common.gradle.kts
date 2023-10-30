plugins {
    `java-library`
    `jvm-test-suite`
    id("org.jetbrains.kotlin.jvm")
    id("jacoco")
    id("jacoco-report-aggregation")
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}


kotlin {
    jvmToolchain(19)
}


testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.UNIT_TEST)
        }

        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.INTEGRATION_TEST)

            sources {
                kotlin {
                    srcDir("src/integrationTest/kotlin")
                }
                resources {
                    setSrcDirs(
                        listOf(
                            "src/main/resources",
                            "src/test/resources",
                            "src/integrationTest/resources"
                        )
                    )
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

        val securityTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()

            sources {
                kotlin {
                    srcDir("src/securityTest/kotlin")
                }
                resources {
                    setSrcDirs(
                        listOf(
                            "src/main/resources",
                            "src/test/resources",
                            "src/securityTest/resources"
                        )
                    )
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
    dependsOn(testing.suites.named("securityTest"))

}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).apply {
                exclude(
                    "**/**/*serializer*.*",
                    "**/**/*Companion*.*"
                )
            }
        }))
    }
    dependsOn(tasks.check)
}
