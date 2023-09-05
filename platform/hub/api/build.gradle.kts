import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("hub-backend")

dependencies {
    implementation(project(":platform:lib:sdk"))
    implementation(project(":platform:lib:kua"))

    implementation(project(":platform:hub:core"))
    implementation(project(":platform:hub:repository:api"))
    implementation(project(":platform:hub:repository:memory"))
    implementation(project(":platform:hub:repository:sqlite"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":platform:runner-extension:std:log"))
}


@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {

                dependencies {
                    implementation(project())
                    implementation(project(":platform:lib:sdk"))

                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }

                    implementation(project(":platform:hub:api"))
                    implementation(project(":platform:hub:admin"))
                    implementation(project(":platform:runner"))
                    implementation(project(":platform:hub:repository:api"))
                    implementation(project(":platform:hub:repository:memory"))

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