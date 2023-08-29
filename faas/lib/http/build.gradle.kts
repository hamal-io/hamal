plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(project(":faas:lib:common"))
    implementation(external.apache.http.client)
    implementation(external.apache.http.mime)
    implementation(external.apache.commons.logging)

    testImplementation(project(":faas:lib:http"))

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":faas:lib:common"))
                    implementation(external.apache.http.client)
                    implementation(external.apache.http.mime)
                    implementation(external.apache.commons.logging)

                    implementation(project(":faas:lib:http"))

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