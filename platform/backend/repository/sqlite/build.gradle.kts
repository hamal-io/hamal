import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}

archivesName.set("platform-repository-sqlite")

dependencies {
    api(project(":platform:lib:sqlite"))
    api(project(":platform:backend:repository:record"))

    testImplementation(project(":platform:backend:repository:sqlite"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:sqlite"))

                    implementation(project(":platform:backend:repository:sqlite"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}