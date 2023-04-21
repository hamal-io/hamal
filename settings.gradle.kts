rootProject.name = "hamal"

include("application")

include(":script:api")
include(":script:impl")

include(":lib:ddd")
include(":lib:domain")
include(":lib:domain-value-object")
include(":lib:domain-notification")
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


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
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
            library("h2", "com.h2database", "h2").withoutVersion()
            library("hikari", "com.zaxxer", "HikariCP").withoutVersion()
        }
    }
}