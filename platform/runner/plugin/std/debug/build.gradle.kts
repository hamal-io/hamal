plugins {
    id("hamal.extension")
}


tasks.jar {
    archiveFileName.set("cap-std-debug.jar")
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
}
