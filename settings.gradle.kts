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

include(":platform:hub")
include(":platform:hub:admin")
include(":platform:hub:api")
include(":platform:hub:bootstrap")
include(":platform:hub:bridge")
include(":platform:hub:core")
include(":platform:hub:repository:api")
include(":platform:hub:repository:memory")
include(":platform:hub:repository:record")
include(":platform:hub:repository:sqlite")
include(":platform:hub:repository:testbed")
include(":platform:mono")

include(":platform:runner")
include(":platform:runner-extension")
include(":platform:runner-extension:net:http")
include(":platform:runner-extension:std:debug")
include(":platform:runner-extension:std:decimal")
include(":platform:runner-extension:std:log")
include(":platform:runner-extension:std:test")
include(":platform:runner-extension:std:sys")
include(":platform:runner-extension:starter")
include(":platform:runner-extension:web3")

include(":platform:lib")
include(":platform:lib:common")
include(":platform:lib:domain")
include(":platform:lib:http")
include(":platform:lib:kua")
include(":platform:lib:sdk")
include(":platform:lib:sqlite")
include(":platform:lib:web3")

include(":project")
include(":project:fn_guru")
include(":project:web3proxy")

rootProject.name = "hamal"

