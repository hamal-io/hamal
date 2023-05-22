import com.github.gradle.node.npm.proxy.ProxySettings
import com.github.gradle.node.yarn.task.YarnTask
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
    id("com.github.node-gradle.node").version("5.0.0")
}

node {
    nodeProjectDir.set(file("${project.projectDir}/src/main/resources"))
    nodeProxySettings.set(ProxySettings.SMART)
}

archivesName.set("frontend")



dependencies {
    implementation(external.spring.web)
    compileOnly(external.spring.devTools)
}

tasks.bootJar {
    enabled = false
}

val yarnBuild = tasks.register<YarnTask>("yarnBuild") {
    description = "Creates the frontend build"
    group = "Yarn"
    yarnCommand.set(listOf("run", "build"))
}

tasks.register<YarnTask>("yarnInstall") {
    description = "Installs dependencies"
    group = "Yarn"
    yarnCommand.set(listOf("install"))
}

val yarnClean = tasks.register<YarnTask>("yarnClean") {
    description = "Installs dependencies"
    group = "Yarn"
    yarnCommand.set(listOf("cache", "clean"))
    delete("${project.projectDir}/src/main/resources/dist")
}


tasks.register<YarnTask>("yarnDev") {
    description = "Runs the application in dev mode"
    group = "Yarn"
    yarnCommand.set(listOf("run", "dev"))
}

tasks.compileKotlin {
    dependsOn(yarnBuild)
}

tasks.clean{
    dependsOn(yarnClean)
}

apply(plugin = "io.spring.dependency-management")