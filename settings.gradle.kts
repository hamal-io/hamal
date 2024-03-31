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
            library("gson", "com.google.code.gson", "gson").version("2.10.1")

            library("spring-web", "org.springframework.boot", "spring-boot-starter-web").version("3.2.3")
            library("spring-mail", "org.springframework.boot", "spring-boot-starter-mail").version("3.2.3")
            library("spring-test", "org.springframework.boot", "spring-boot-starter-test").version("3.2.3")
            library("spring-logging", "org.springframework.boot", "spring-boot-starter-logging").version("3.2.3")
            library("spring-schedule", "org.springframework", "spring-context").version("6.1.4")

            library("sqlite", "org.xerial", "sqlite-jdbc").version("3.41.2.1")

            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").version("1.9.23")

            library("apache-http-client", "org.apache.httpcomponents", "httpclient").version("4.5.13")
            library("apache-http-mime", "org.apache.httpcomponents", "httpmime").version("4.5.13")
            library("apache-commons-logging", "commons-logging", "commons-logging").version("1.2")
            library("apache-commons-compress", "org.apache.commons", "commons-compress").version("1.26.1")

            library("bouncycastle", "org.bouncycastle", "bcprov-jdk18on").version("1.77")
            library("web3j-crypto", "org.web3j", "crypto").version("4.11.1")
        }
    }
}

include(":platform:backend")
include(":platform:backend:api")
include(":platform:backend:bridge")
include(":platform:backend:core")
include(":platform:backend:repository:api")
include(":platform:backend:repository:memory")
include(":platform:backend:repository:record")
include(":platform:backend:repository:sqlite")
include(":platform:backend:repository:testbed")

include(":platform:lib")
include(":platform:lib:common")
include(":platform:lib:domain")
include(":platform:lib:http")
include(":platform:lib:kua")
include(":platform:lib:sdk")
include(":platform:lib:sqlite")
include(":platform:lib:web3")

include(":platform:runner:runner")

include(":platform:runner:extension:net:smtp")
include(":platform:runner:extension:net:http")
include(":platform:runner:extension:std:decimal")
include(":platform:runner:extension:std:log")
include(":platform:runner:extension:telegram")
include(":platform:runner:extension:web3")

include(":platform:runner:plugin:net:smtp")
include(":platform:runner:plugin:net:http")
include(":platform:runner:plugin:std:debug")
include(":platform:runner:plugin:std:log")
include(":platform:runner:plugin:std:sys")
include(":platform:runner:plugin:starter")
include(":platform:runner:plugin:web3")

include(":platform:runner:test")

include(":platform:testbed")

include(":app")
include(":app:candy-rocks")
include(":app:fn-guru")
include(":app:hamal-backend")
include(":app:hamal-mono")
include(":app:hamal-runner")
include(":app:nyanbot")
include(":app:web3proxy")

rootProject.name = "hamal"

