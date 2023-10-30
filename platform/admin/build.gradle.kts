import com.github.gradle.node.npm.proxy.ProxySettings
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.node-gradle.node").version("5.0.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}


kotlin {
    jvmToolchain(19)
}

node {
    nodeProjectDir.set(file("${project.projectDir}/src/main/resources"))
    nodeProxySettings.set(ProxySettings.SMART)
}

dependencies {
    implementation(project(":platform:lib:sdk"))
    implementation(project(":platform:backend:core"))
}

val npmBuild = tasks.register<NpmTask>("npmBuild") {
    description = "Creates the frontend build"
    group = "Npm"
    npmCommand.set(listOf("run", "build"))
}


val npmClean = tasks.register<NpmTask>("npmClean") {
    description = "Installs dependencies"
    group = "Npm"
    npmCommand.set(listOf("cache", "clean"))
    delete("${project.projectDir}/src/main/resources/dist")
}


tasks.register<NpmTask>("npmDev") {
    description = "Runs the application in dev mode"
    group = "Npm"
    npmCommand.set(listOf("run", "dev"))
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {

                dependencies {
                    implementation(project())
                    implementation(project(":platform:lib:sdk"))
                    implementation(project(":platform:backend:bridge"))
                    implementation(project(":platform:backend:core"))
                    implementation(project(":platform:runner"))

                    implementation(external.junit)
                    implementation(external.hamcrest)
                    implementation(external.spring.test) {
                        exclude("org.assertj", "*")
                    }
                }
            }
        }
    }
}