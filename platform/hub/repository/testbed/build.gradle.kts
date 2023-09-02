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
                    implementation(project(":platform:hub:repository:api"))
                    implementation(project(":platform:hub:repository:memory"))
                    implementation(project(":platform:hub:repository:sqlite"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}