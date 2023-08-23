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
                    implementation(project(":faas:instance:repository:api"))
                    implementation(project(":faas:instance:repository:memory"))
                    implementation(project(":faas:instance:repository:sqlite"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}