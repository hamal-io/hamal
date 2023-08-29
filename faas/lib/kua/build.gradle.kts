plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":faas:lib:common"))
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

                    implementation(project(":faas:lib:kua"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}