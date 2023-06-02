
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":lib:domain"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":backend:impl"))
    implementation(project(":frontend"))
    implementation(project(":agent:impl"))

    testImplementation(project(":lib:sdk"))
    testImplementation(project(":bootstrap"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
    testImplementation(external.spring.test) {
        exclude("org.assertj", "*")
    }
    compileOnly(external.spring.devTools)
}

tasks.named<BootJar>("bootJar") {
    launchScript()
}


//testing {
//    suites {
//        val test by getting(JvmTestSuite::class) {
//            useJUnitJupiter()
//        }
//
//        val integrationTestMemory by registering(JvmTestSuite::class) {
//            sources {
//                kotlin {
//                    setSrcDirs(listOf("src/integrationTestMemory/kotlin"))
//                }
//            }
//
//            targets {
//                all {
//                    testTask.configure {
//                        shouldRunAfter(test)
//                    }
//                }
//            }
//        }
//
//        val integrationTestSqlite by registering(JvmTestSuite::class) {
//            sources {
//                kotlin {
//                    setSrcDirs(listOf("src/integrationTestSqlite/kotlin"))
//                }
//            }
//
//            targets {
//                all {
//                    testTask.configure {
//                        shouldRunAfter(test)
//                    }
//                }
//            }
//        }
//    }
//}

//reporting {
//    reports {
//        val testCodeCoverageReport by creating(JacocoCoverageReport::class) {
//            testType.set(TestSuiteType.UNIT_TEST)
//        }
//    }
//}

