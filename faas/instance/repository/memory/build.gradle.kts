import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("instance-repository-memory")

dependencies {
    api(project(":faas:instance:repository:record"))

    testImplementation(project(":faas:instance:repository:memory"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(external.sqlite)

                    implementation(project(":faas:instance:repository:memory"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}