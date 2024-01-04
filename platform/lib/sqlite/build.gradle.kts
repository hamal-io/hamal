plugins {
    id("hamal.common")
}

dependencies {
    implementation(project(":platform:lib:common"))
    api(external.sqlite)

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:common"))
                    implementation(external.sqlite)
                    implementation(project(":platform:lib:sqlite"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}