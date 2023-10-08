plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
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
                    implementation(project(":platform:runner-extension:std:sys"))
                    implementation(project(":platform:runner-extension:std:log"))

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