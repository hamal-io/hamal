import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
    id("com.bmuschko.docker-spring-boot-application") version "9.3.1"
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("hub-bootstrap")

docker {
    springBootApplication {
        maintainer.set("hamal.io docker@hamal.io")
        baseImage.set("openjdk:22-slim-bullseye")
        ports.set(listOf(8008, 8008))
        images.set(listOf("hamalio/instance"))
        jvmArgs.set(listOf("-Xmx4096m", "-XX:+ExitOnOutOfMemoryError"))
    }
}

dependencies {
    implementation(project(":platform:lib:sdk"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":platform:hub:backend"))
    implementation(project(":platform:hub:admin"))
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:sdk"))

                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }

                    implementation(project(":platform:lib:kua"))
                    implementation(project(":platform:hub:backend"))
                    implementation(project(":platform:hub:repository:api"))
                    implementation(project(":platform:hub:admin"))

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