plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
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