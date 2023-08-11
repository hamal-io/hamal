import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
    id("com.bmuschko.docker-spring-boot-application") version "9.3.1"
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("mono")

docker {
    springBootApplication {
        maintainer.set("hamal.io docker@hamal.io")
        baseImage.set("openjdk:22-slim-bullseye")
        ports.set(listOf(8008, 8008))
        images.set(listOf("hamalio/faas-mono"))
        jvmArgs.set(listOf("-Xmx4096m", "-XX:+ExitOnOutOfMemoryError"))
    }
}

dependencies {
    implementation(project(":lib:sdk"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":faas:instance:backend"))
    implementation(project(":faas:instance:frontend"))
    implementation(project(":faas:runner"))
}

tasks.named<BootJar>("bootJar") {
    from(project.files("${project.projectDir}/../../lib/kua/src/main/resources/")) {
        include("*.so")
    }
    launchScript()
}


val copyKuaLibs = tasks.register<Copy>("copyKuaLibs") {
    from(project.files("${project.projectDir}/../../lib/kua/src/main/resources/")) {
        include("*.so")
    }
    into(layout.buildDirectory.dir("resources/integrationTest/"))
}

tasks.named<Test>("integrationTest") {
    dependsOn(copyKuaLibs)
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":lib:sdk"))

                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }

                    implementation(project(":lib:kua"))
                    implementation(project(":faas:instance:backend"))
                    implementation(project(":faas:instance:repository:api"))
                    implementation(project(":faas:instance:frontend"))
                    implementation(project(":faas:runner"))
                    implementation(project(":extension:std:sys"))
                    implementation(project(":extension:std:log"))

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