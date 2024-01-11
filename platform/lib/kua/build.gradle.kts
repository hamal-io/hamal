plugins {
    id("hamal.common")
}

dependencies {
    implementation(project(":platform:lib:common"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(external.spring.logging)

                    implementation(project(":platform:lib:kua"))
                    implementation(project(":platform:lib:domain"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}