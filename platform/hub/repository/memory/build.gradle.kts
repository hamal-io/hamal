import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("instance-repository-memory")

dependencies {
    api(project(":platform:hub:repository:record"))

    testImplementation(project(":platform:hub:repository:memory"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(external.sqlite)

                    implementation(project(":platform:hub:repository:memory"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}