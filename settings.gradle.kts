plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}



toolchainManagement {
    jvm {
    }
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

include(":platform:instance")
include(":platform:instance:backend")
include(":platform:instance:bootstrap")
include(":platform:instance:frontend")
include(":platform:instance:repository:api")
include(":platform:instance:repository:memory")
include(":platform:instance:repository:record")
include(":platform:instance:repository:sqlite")
include(":platform:instance:repository:testbed")
include(":platform:mono")
include(":platform:runner")

include(":platform:extension")
include(":platform:extension:net:http")
include(":platform:extension:std:debug")
include(":platform:extension:std:decimal")
include(":platform:extension:std:log")
include(":platform:extension:std:test")
include(":platform:extension:std:sys")
include(":platform:extension:starter")
include(":platform:extension:web3")

include(":platform:lib")
include(":platform:lib:common")
include(":platform:lib:domain")
include(":platform:lib:http")
include(":platform:lib:kua")
include(":platform:lib:sdk")
include(":platform:lib:sqlite")
include(":platform:lib:web3")

include(":web3proxy")

rootProject.name = "hamal"

