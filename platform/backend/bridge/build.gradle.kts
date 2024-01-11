import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}

archivesName.set("platform-bridge")

dependencies {
    implementation(project(":platform:lib:sdk"))
    implementation(project(":platform:backend:core"))
}


@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {

                dependencies {
                    implementation(project())
                    implementation(project(":platform:lib:sdk"))
                    implementation(project(":platform:backend:core"))
                    implementation(project(":platform:runner"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                    implementation(external.spring.test) {
                        exclude("org.assertj", "*")
                    }
                }
            }
        }
    }
}