plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:common"))
    api(external.sqlite)

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":lib:common"))
                    implementation(external.sqlite)
                    implementation(project(":lib:sqlite"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}