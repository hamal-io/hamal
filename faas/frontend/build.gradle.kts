import com.github.gradle.node.npm.proxy.ProxySettings
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.node-gradle.node").version("5.0.0")
}

kotlin {
    jvmToolchain(19)
}

node {
    nodeProjectDir.set(file("${project.projectDir}/src/main/resources"))
    nodeProxySettings.set(ProxySettings.SMART)
}

dependencies {
    implementation(external.spring.web)
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