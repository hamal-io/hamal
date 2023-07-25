plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":lib:common"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

project.sourceSets {
    main {
        resources {
            setSrcDirs(project.files("${project.projectDir}/src/main/resources/"))
        }
    }
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