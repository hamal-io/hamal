plugins {
    id("hamal.extension")
}


tasks.jar {
    archiveFileName.set("ext-web3-eth.jar")
}

distributions {
    main {
        contents {
            into("lib/") {  // Copy the following jars to the lib/ directory in the distribution archive
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
    implementation(project(":platform:lib:sdk"))
}


@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":platform:runner:test"))
                    implementation(project(":platform:runner:plugin:web3"))
                    implementation(project(":platform:runner:extension:web3:eth"))
                    implementation(project(":platform:lib:web3"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                    implementation(external.spring.web) {
                        exclude("com.fasterxml.jackson.core", "jackson-core")
                        exclude("org.springframework.boot", "spring-boot-starter-json")
                        exclude("com.fasterxml.jackson.core", "jackson-annotations")
                    }
                    implementation(external.spring.test) {
                        exclude("org.assertj", "*")
                    }
                }
            }
        }
    }
}