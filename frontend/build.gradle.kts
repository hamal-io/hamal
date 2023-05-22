import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.common")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.springframework.boot").version("3.0.5")
}
archivesName.set("frontend")

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation(external.spring.web)
    compileOnly(external.spring.devTools)
}

tasks.bootJar {
    enabled = false
}