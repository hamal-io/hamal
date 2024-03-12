import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.9.23")
    id("com.bmuschko.docker-spring-boot-application") version "9.3.1"
}

apply(plugin = "io.spring.dependency-management")

docker {
    springBootApplication {
        maintainer.set("hamal.io docker@hamal.io")
        baseImage.set("openjdk:22-slim-bullseye")
        ports.set(listOf(10000, 10000))
        images.set(listOf("hamalio/web3proxy"))
        jvmArgs.set(listOf("-Dspring.profiles.active=default", "-Xmx8192m", "-XX:+ExitOnOutOfMemoryError"))
    }
}


dependencies {
    implementation(project(":platform:lib:sqlite"))
    implementation(project(":platform:lib:web3"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }
}

tasks.named<BootJar>("bootJar") {
    launchScript()
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":app:web3proxy"))
                    implementation(project(":platform:lib:sqlite"))
                    implementation(project(":platform:lib:web3"))

                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }

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