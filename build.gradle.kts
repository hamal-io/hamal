buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.10")
    }

}

apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
