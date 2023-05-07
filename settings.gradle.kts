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
            library("spring-devTools", "org.springframework.boot", "spring-boot-devtools").withoutVersion()
            library("spring-logging", "org.springframework.boot", "spring-boot-starter-logging").version("3.0.5")
            library("sqlite", "org.xerial", "sqlite-jdbc").version("3.41.2.1")
            library("hikari", "com.zaxxer", "HikariCP").withoutVersion()

            library("kotlin-protobuf", "org.jetbrains.kotlinx", "kotlinx-serialization-protobuf").version("1.5.0")
            library("kotlin-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.5.0")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").version("1.8.10")
        }
    }
}


include("bootstrap")

include(":lib:core")
include(":lib:script:api")
include(":lib:script:impl")

include(":backend")
include(":backend:usecase")
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

rootProject.name = "hamal"

