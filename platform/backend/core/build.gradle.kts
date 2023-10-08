import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

archivesName.set("hub-backend")

dependencies {
    api(project(":platform:lib:sdk"))
    api(project(":platform:lib:kua"))
    api(project(":platform:backend:request"))

    api(project(":platform:backend:repository:api"))
    api(project(":platform:backend:repository:memory"))
    api(project(":platform:backend:repository:sqlite"))

    api(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    api(project(":platform:runner-extension:std:log"))
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

                    implementation(project(":platform:backend:api"))
                    implementation(project(":platform:runner"))
                    implementation(project(":platform:backend:repository:api"))
                    implementation(project(":platform:backend:repository:memory"))

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