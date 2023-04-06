rootProject.name = "hamal"

include("application")

include("lib")

include(":lib:ddd")
include(":lib:domain")
include(":lib:domain-value-object")
include(":lib:meta")
include(":lib:util")

include(":module")
include(":module:launchpad")
include(":module:launchpad:application")
include(":module:launchpad:core")
include(":module:launchpad:infra")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    versionCatalogs {
        create("external") {
            library("junit", "org.junit.jupiter:junit-jupiter:5.9.1")
            library("hamcrest", "org.hamcrest:hamcrest:2.2")

            library("springWeb", "org.springframework.boot:spring-boot-starter-web:3.0.5")
            library("springTest", "org.springframework.boot:spring-boot-starter-test:3.0.5")
            library("springDataJdbc", "org.springframework.data:spring-data-jdbc:2.1.6")
            library("h2", "com.h2database:h2:2.1.214")
            library("hikari", "com.zaxxer:HikariCP:5.0.1")
        }
    }
}