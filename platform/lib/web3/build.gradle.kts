plugins {
    id("hamal.common")
}

dependencies {
    api(project(":platform:lib:http"))
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
}


testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:http"))
                    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

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