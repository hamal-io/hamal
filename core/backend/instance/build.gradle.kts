import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "backend"
archivesName.set("backend-impl")

dependencies {
    implementation(project(":lib:sdk"))
    implementation(project(":lib:script:impl"))

    implementation(project(":core:backend:repository:api"))
    implementation(project(":core:backend:repository:memory"))
    implementation(project(":core:backend:repository:sqlite"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":core:agent:extension:api"))
    implementation(project(":core:agent:extension:std:log"))
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

                    implementation(project(":core:backend:instance"))
                    implementation(project(":core:frontend"))
                    implementation(project(":core:agent:impl"))
                    implementation(project(":core:backend:repository:api"))
                    implementation(project(":core:backend:repository:memory"))

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