import gradle.kotlin.dsl.accessors._4062691b52bb2ae8e3287a873466e0d9.main
import gradle.kotlin.dsl.accessors._4062691b52bb2ae8e3287a873466e0d9.sourceSets

plugins {
    `java-library`
    `jvm-test-suite`
    id("org.jetbrains.kotlin.jvm")
    id("jacoco")
    id("jacoco-report-aggregation")
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
                resources{
                    setSrcDirs(listOf(
                        "src/main/resources",
                        "src/test/resources",
                        "src/integrationTest/resources"
                    ))
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
