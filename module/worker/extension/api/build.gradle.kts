import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("hamal.kotlin-application-conventions")
    id("org.springframework.boot").version("3.0.5")
}

apply(plugin = "io.spring.dependency-management")

archivesName.set("extension-api")

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-notification"))
    implementation(project(":lib:domain-value-object"))
    implementation(project(":module:worker:application"))
    implementation(project(":module:worker:core"))


    implementation(external.springWeb) {
//        exclude("com.fasterxml.jackson.core", "jackson-core")
//        exclude("org.springframework.boot", "spring-boot-starter-json")
//        exclude("com.fasterxml.jackson.core", "jackson-annotations")
//        exclude("org.springframework.boot", "spring-boot-starter-json")
    }

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

distributions {
    main {
//        baseName = project.name
        contents {
            into("lib/") {  // Copy the following jars to the lib/ directory in the distribution archive
//                from jar
//                        from configurations.runtimeClasspath
//                        dirMode = 0755
                fileMode = 644
            }
            from("src/main/kotlin") {  // Contents of this directory are copied by default
                dirMode = 755;
                fileMode = 644
            }
            from("src/main/resources") {  // Contents of this directory are copied by default
                dirMode = 755;
                fileMode = 644
            }
        }
    }
}
