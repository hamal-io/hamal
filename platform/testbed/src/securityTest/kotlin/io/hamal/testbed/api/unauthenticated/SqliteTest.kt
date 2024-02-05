//package io.hamal.testbed.api.unauthenticated
//
//import io.hamal.api.ApiConfig
//import io.hamal.core.CoreConfig
//import org.junit.jupiter.api.TestInstance
//import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
//import org.springframework.boot.Banner.Mode.OFF
//import org.springframework.boot.WebApplicationType.NONE
//import org.springframework.boot.WebApplicationType.SERVLET
//import org.springframework.boot.builder.SpringApplicationBuilder
//
//@TestInstance(PER_CLASS)
//internal object SqliteApiUnauthenticatedTest : BaseApiUnauthenticatedTest(
//    "http://localhost:8041"
//) {
//    init {
//        val applicationBuilder = SpringApplicationBuilder()
//            .parent(CoreConfig::class.java, TestConfig::class.java)
//            .profiles("test", "api", "unauthenticated", "sqlite")
//            .bannerMode(OFF)
//            .web(NONE)
//
//        applicationBuilder.run().let {
//            applicationBuilder
//                .parent(it)
//                .child(ApiConfig::class.java)
//                .web(SERVLET)
//                .properties("server.port=8041")
//                .bannerMode(OFF)
//                .run()
//        }
//    }
//}