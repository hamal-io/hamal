import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.withType<Test> {
    forkEvery = 1
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1).also {
        println("Testbed parallel fork to $it")
    }
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:sdk"))

                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }

                    implementation(project(":platform:backend:api"))
                    implementation(project(":platform:backend:bridge"))
                    implementation(project(":platform:backend:core"))
                    implementation(project(":platform:runner"))

                    implementation(project(":platform:backend:repository:api"))
                    implementation(project(":platform:runner-test"))
                    implementation(project(":platform:runner-plugin:net:http"))

                    implementation(project(":platform:runner-plugin:std:debug"))
                    implementation(project(":platform:runner-plugin:std:sys"))
                    implementation(project(":platform:runner-plugin:std:log"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                    implementation(external.spring.test) {
                        exclude("org.assertj", "*")
                    }
                }
            }
        }
    }
}