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

    implementation(project(":platform:runner-extension:net:http"))
    implementation(project(":platform:runner-extension:std:decimal"))
    implementation(project(":platform:runner-extension:std:log"))
    implementation(project(":platform:runner-extension:telegram"))

    implementation(project(":platform:runner-plugin:net:http"))
    implementation(project(":platform:runner-plugin:std:debug"))
    implementation(project(":platform:runner-plugin:std:log"))
    implementation(project(":platform:runner-plugin:std:sys"))
    implementation(project(":platform:runner-plugin:web3"))

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
                    implementation(project(":platform:runner-test"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}