import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("hamal.common")
    application
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}
apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(project(":platform:lib:sdk"))
    implementation(project(":platform:lib:web3"))
    implementation(project(":platform:lib:kua"))

    implementation(project(":platform:runner-capability:net:http"))
    implementation(project(":platform:runner-capability:std:decimal"))
    implementation(project(":platform:runner-capability:std:debug"))
    implementation(project(":platform:runner-capability:std:log"))
    implementation(project(":platform:runner-capability:std:sys"))

    implementation(external.spring.web) {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
        exclude("com.fasterxml.jackson.core", "jackson-core")
        exclude("org.springframework.boot", "spring-boot-starter-json")
        exclude("com.fasterxml.jackson.core", "jackson-annotations")
    }

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.named<BootJar>("bootJar") { enabled = false }

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:lib:sdk"))
                    implementation(project(":platform:lib:kua"))
                    implementation(project(":platform:runner"))
                    implementation(project(":platform:runner-capability:std:test"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}