import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
}
group = "script"
archivesName.set("script-impl")

dependencies {
    api(project(":lib:domain"))
    api(project(":lib:script:api"))
    implementation(external.spring.logging)

    testImplementation(project(":lib:script:api"))
    testImplementation(project(":lib:script:impl"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project(":lib:domain"))
                    implementation(project(":lib:script:api"))
                    implementation(external.spring.logging)

                    implementation(project(":lib:script:api"))
                    implementation(project(":lib:script:impl"))
                    implementation(external.junit)
                    implementation(external.hamcrest)
                }
            }
        }
    }
}