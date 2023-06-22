import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-memory")

dependencies {
    api(project(":backend:repository:record"))

    testImplementation(project(":backend:repository:memory"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(external.sqlite)

                    implementation(project(":backend:repository:memory"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}