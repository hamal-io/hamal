plugins {
    id("hamal.extension")
}

tasks.jar {
    archiveFileName.set("ext-std-once.jar")
}

distributions {
    main {
        contents {
            into("lib/") {  // Copy the following jars to the lib/ directory in the distribution archive
                fileMode = 644
            }
            from("src/main/kotlin") {  // Contents of this directory are copied by default
                dirMode = 755
                fileMode = 644
            }
            from("src/main/resources") {  // Contents of this directory are copied by default
                dirMode = 755
                fileMode = 644
            }
        }
    }
}

dependencies {
    implementation(project(":platform:lib:kua"))
    implementation(project(":platform:runner:extension:std:throw"))
    implementation(project(":platform:runner:extension:std:table"))
}


@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:runner:test"))
                    implementation(project(":platform:runner:extension:std:once"))
                    implementation(project(":platform:runner:extension:std:table"))
                    implementation(project(":platform:runner:extension:std:throw"))
                }
            }
        }
    }
}