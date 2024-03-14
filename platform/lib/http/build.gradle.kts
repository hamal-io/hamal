plugins {
    id("hamal.common")
}

dependencies {
    api(project(":platform:lib:domain"))
    implementation(external.apache.http.client)
    implementation(external.apache.http.mime)
    implementation(external.apache.commons.logging)

    testImplementation(project(":platform:lib:http"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:domain"))
                    implementation(external.apache.http.client)
                    implementation(external.apache.http.mime)
                    implementation(external.apache.commons.logging)

                    implementation(project(":platform:lib:http"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }
                    implementation(external.spring.test) {
                        exclude("org.assertj", "*")
                    }
                }
            }
        }
    }
}