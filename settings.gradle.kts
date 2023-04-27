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

            library("spring-web", "org.springframework.boot", "spring-boot-starter-web").withoutVersion()
            library("spring-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("spring-jdbc", "org.springframework.data", "spring-data-jdbc").withoutVersion()
            library("spring-devTools", "org.springframework.boot", "spring-boot-devtools").withoutVersion()
            library("sqlite", "org.xerial", "sqlite-jdbc").version("3.41.2.1")
            library("hikari", "com.zaxxer", "HikariCP").withoutVersion()

            library("kotlin-protobuf", "org.jetbrains.kotlinx", "kotlinx-serialization-protobuf").version("1.5.0")
            library("kotlin-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.5.0")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").version("1.8.10")
        }
    }
}


include("bootstrap")

include(":script:api")
include(":script:impl")

include(":lib:ddd")
include(":lib:domain-value-object")
include(":lib:log")
include(":lib:meta")
include(":lib:util")

include(":backend")
include(":backend:application")
include(":backend:core")
include(":backend:infra")
include(":backend:repository:api")
include(":backend:repository:memory")
include(":backend:repository:sqlite")

include(":worker:application")
include(":worker:core")
include(":worker:infra")
include(":worker:extension:api")
include(":worker:extension:impl:starter")


//include(":repository:api")
//include(":repository:memory")
//include(":repository:sqlite")
//
//include(":module:launchpad:application")
//include(":module:launchpad:core")
//include(":module:launchpad:infra")
//
//include(":module:queue:application")
//include(":module:queue:core")
//include(":module:queue:infra")
//
//include(":worker:application")
//include(":worker:extension:api")
//include(":worker:extension:impl:starter")
//
//include(":worker:core")
//include(":worker:infra")


rootProject.name = "hamal"

