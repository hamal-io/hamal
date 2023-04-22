
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

            library("springWeb", "org.springframework.boot", "spring-boot-starter-web").withoutVersion()
            library("springTest", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("springDataJdbc", "org.springframework.data", "spring-data-jdbc").withoutVersion()
            library("springDevTools", "org.springframework.boot", "spring-boot-devtools").withoutVersion()
            library("sqlite", "org.xerial", "sqlite-jdbc").version("3.41.2.1")
            library("hikari", "com.zaxxer", "HikariCP").withoutVersion()
        }
    }
}


include("application")

include(":script:api")
include(":script:impl")

include(":lib:ddd")
include(":lib:domain")
include(":lib:domain-value-object")
include(":lib:domain-notification")
include(":lib:log")
include(":lib:meta")
include(":lib:util")

include(":module:launchpad:application")
include(":module:launchpad:core")
include(":module:launchpad:infra")

include(":module:queue:application")
include(":module:queue:core")
include(":module:queue:infra")

include(":module:worker:application")
include(":module:worker:extension:api")
include(":module:worker:extension:impl:starter")


include(":module:worker:core")
include(":module:worker:infra")



rootProject.name = "hamal"

