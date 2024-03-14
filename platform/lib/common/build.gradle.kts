plugins {
    id("hamal.common")
}

dependencies {
    api(external.kotlin.reflect)
    api(external.spring.logging)
    api(external.gson)
    implementation(external.apache.commons.compress)
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
                    implementation(external.kotlin.reflect)
                    implementation(external.spring.logging)

                    implementation(project(":platform:lib:common"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}