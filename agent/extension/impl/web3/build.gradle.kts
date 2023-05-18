plugins {
    id("hamal.extension")

}


tasks.jar {
    archiveFileName.set("extension-web3.jar")
}

distributions {
    main {
        contents {
            into("lib/") {  // Copy the following jars to the lib/ directory in the distribution archive
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

dependencies {
    implementation(project(":agent:extension:api"))
    implementation(project(":lib:common"))
    implementation(project(":lib:web3"))
}
