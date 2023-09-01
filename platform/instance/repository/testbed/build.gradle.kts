plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:instance:repository:api"))
                    implementation(project(":platform:instance:repository:memory"))
                    implementation(project(":platform:instance:repository:sqlite"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}