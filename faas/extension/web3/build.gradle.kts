plugins {
    id("hamal.extension")

}


tasks.jar {
    archiveFileName.set("extension-web3.jar")
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
    implementation(project(":faas:lib:kua"))
    implementation(project(":faas:lib:web3"))
}


@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":faas:extension:std:test"))
                    implementation(project(":faas:extension:web3"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}