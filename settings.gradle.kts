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
        }
    }
}