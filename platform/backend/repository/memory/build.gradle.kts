import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}

archivesName.set("platform-repository-memory")

dependencies {
    api(project(":platform:backend:repository:record"))

    testImplementation(project(":platform:backend:repository:memory"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(external.sqlite)

                    implementation(project(":platform:backend:repository:memory"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}