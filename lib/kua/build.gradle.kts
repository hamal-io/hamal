plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

project.sourceSets {
    integrationTest {
        resources {
            setSrcDirs(project.files("${project.projectDir}/src/main/resources/"))
        }
    }
    test {
        resources {
            setSrcDirs(project.files("${project.projectDir}/src/main/resources/"))
        }
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(external.spring.logging)

                    implementation(project(":lib:kua"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}