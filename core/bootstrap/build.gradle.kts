import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    kotlin("plugin.spring").version("1.8.10")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":lib:sdk"))

    implementation(external.spring.web) {
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    implementation(project(":core:backend:instance"))
    implementation(project(":core:frontend"))
    implementation(project(":core:agent"))
}

tasks.named<BootJar>("bootJar") {
    launchScript()
}

project.sourceSets {
    main {
        resources {
            setSrcDirs(project.files("${project.projectDir}/../../lib/kua/src/main/resources/"))
        }
    }
}

tasks.named<Jar>("jar") {
    from("${project.projectDir}/../../lib/kua/src/main/resources/") {
        include("*.so")
        into("$projectDir/build/tmp/")
    }
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
                    implementation(project(":core:backend:instance"))
                    implementation(project(":core:backend:repository:api"))
                    implementation(project(":core:frontend"))
                    implementation(project(":core:agent"))
                    implementation(project(":extension:api"))
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