plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }

    versionCatalogs {
        create("external") {
            library("junit", "org.junit.jupiter:junit-jupiter:5.9.1")
            library("hamcrest", "org.hamcrest:hamcrest:2.2")

            library("spring-web", "org.springframework.boot", "spring-boot-starter-web").version("3.0.5")
            library("spring-test", "org.springframework.boot", "spring-boot-starter-test").version("3.0.5")
            library("spring-devTools", "org.springframework.boot", "spring-boot-devtools").version("3.0.5")
            library("spring-logging", "org.springframework.boot", "spring-boot-starter-logging").version("3.0.5")
            library("sqlite", "org.xerial", "sqlite-jdbc").version("3.41.2.1")

            library("kotlin-protobuf", "org.jetbrains.kotlinx", "kotlinx-serialization-protobuf").version("1.5.0")
            library("kotlin-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.5.0")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").version("1.8.10")

            library("apache-http-client", "org.apache.httpcomponents", "httpclient").version("4.5.13")
            library("apache-http-mime", "org.apache.httpcomponents", "httpmime").version("4.5.13")
            library("apache-commons-logging", "commons-logging", "commons-logging").version("1.2")
        }
    }
}

include(":application:proxy")

include(":core:backend")

include(":core:agent:impl")
include(":core:agent:extension:api")
include(":core:agent:extension:std:debug")
include(":core:agent:extension:std:sys")
include(":core:agent:extension:std:log")
include(":core:agent:extension:starter")
include(":core:agent:extension:web3")

include(":core:backend")
include(":core:backend:instance")
include(":core:backend:repository:api")
include(":core:backend:repository:memory")
include(":core:backend:repository:record")
include(":core:backend:repository:sqlite")

include(":core:bootstrap")

include(":core:frontend")

include(":lib:common")
include(":lib:domain")
include(":lib:http")
include(":lib:kua")
include(":lib:sdk")
include(":lib:sqlite")
include(":lib:web3")


rootProject.name = "hamal"

