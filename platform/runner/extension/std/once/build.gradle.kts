plugins {
    id("hamal.extension")
}

tasks.jar {
    archiveFileName.set("ext-std-once.jar")
}

distributions {
    main {
        contents {
            into("lib/") {  // Copy the following jars to the lib/ directory in the distribution archive
                fileMode = 644
            }
            from("src/main/kotlin") {  // Contents of this directory are copied by default
                dirMode = 755
                fileMode = 644
            }
            from("src/main/resources") {  // Contents of this directory are copied by default
                dirMode = 755
                fileMode = 644
            }
        }
    }
}

dependencies {
    implementation(project(":platform:lib:kua"))
    implementation(project(":platform:runner:extension:std:throw"))
    implementation(project(":platform:runner:extension:std:table"))
    testImplementation(project(":platform:runner:test"))
}