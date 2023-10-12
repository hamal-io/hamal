plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(external.kotlin.reflect)
    api(external.kotlin.protobuf)
    api(external.kotlin.json)
    api(external.spring.logging)
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
                    implementation(external.kotlin.protobuf)
                    implementation(external.kotlin.json)
                    implementation(external.spring.logging)

                    implementation(project(":platform:lib:common"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}