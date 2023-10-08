plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:backend:repository:api"))
                    implementation(project(":platform:backend:repository:memory"))
                    implementation(project(":platform:backend:repository:sqlite"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}