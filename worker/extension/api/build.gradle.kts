plugins {
    id("hamal.common")
}

dependencies{
    api(project(":lib:script:api"))
}

//
//distributions {
//    main {
////        baseName = project.name
//        contents {
//            into("lib/") {  // Copy the following jars to the lib/ directory in the distribution archive
////                from jar
////                        from configurations.runtimeClasspath
////                        dirMode = 0755
//                fileMode = 644
//            }
//            from("src/main/kotlin") {  // Contents of this directory are copied by default
//                dirMode = 755;
//                fileMode = 644
//            }
//            from("src/main/resources") {  // Contents of this directory are copied by default
//                dirMode = 755;
//                fileMode = 644
//            }
//        }
//    }
//}
