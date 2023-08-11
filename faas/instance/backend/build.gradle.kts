import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("instance-backend")

dependencies {
    implementation(project(":lib:sdk"))
    implementation(project(":lib:kua"))

    implementation(project(":faas:instance:repository:api"))
    implementation(project(":faas:instance:repository:memory"))
    implementation(project(":faas:instance:repository:sqlite"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":extension:std:log"))
}


@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {

                dependencies {
                    implementation(project())
                    implementation(project(":lib:sdk"))

                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }

                    implementation(project(":faas:instance:backend"))
                    implementation(project(":faas:instance:frontend"))
                    implementation(project(":faas:runner"))
                    implementation(project(":faas:instance:repository:api"))
                    implementation(project(":faas:instance:repository:memory"))

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