plugins {
    id("hamal.common")
}

dependencies {
    api(project(":platform:lib:common"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
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