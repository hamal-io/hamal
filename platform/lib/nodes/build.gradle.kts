plugins {
    id("hamal.common")
}

dependencies {
    api(project(":platform:lib:domain"))
    implementation(project(":platform:lib:value"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:kua"))
                    implementation(project(":platform:lib:nodes"))
                    implementation(project(":platform:lib:value"))
                    implementation(project(":platform:runner:extension:std:decimal"))
                    implementation(project(":platform:runner:test"))
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