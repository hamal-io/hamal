import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}
archivesName.set("backend-repository-sqlite")

dependencies {
    api(project(":lib:sqlite"))
    api(project(":faas:backend:repository:record"))

    testImplementation(project(":faas:backend:repository:sqlite"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":lib:sqlite"))

                    implementation(project(":faas:backend:repository:sqlite"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}