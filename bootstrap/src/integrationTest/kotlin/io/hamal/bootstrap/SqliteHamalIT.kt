//package io.hamal.bootstrap
//
//import io.hamal.agent.AgentConfig
//import io.hamal.backend.BackendConfig
//import io.hamal.lib.domain.req.InvokeAdhocReq
//import io.hamal.lib.domain.vo.Code
//import io.hamal.lib.domain.vo.InvocationInputs
//import io.hamal.lib.domain.vo.InvocationSecrets
//import io.hamal.lib.script.api.value.TableValue
//import io.hamal.lib.sdk.DefaultHamalSdk
//import io.hamal.lib.sdk.HamalSdk
//import jakarta.annotation.PostConstruct
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
//import org.springframework.boot.test.web.server.LocalServerPort
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.junit.jupiter.SpringExtension
//
//
//@ExtendWith(SpringExtension::class)
//@ContextConfiguration(
//    classes = [
//        BackendConfig::class,
//        AgentConfig::class
//    ]
//)
//@SpringBootTest(
//    webEnvironment = WebEnvironment.DEFINED_PORT,
//    properties = ["server.port=8084"]
//)
//@ActiveProfiles("memory")
//class SqliteHamalIT(
//    @LocalServerPort val localPort: Int
//) {
//
//
//    @Test
//    fun run() {
//        println("RUNS ON: $localPort")
//
//        val res = sdk.adhocService().submit(
//            InvokeAdhocReq(
//                inputs = InvocationInputs(TableValue()),
//                secrets = InvocationSecrets(listOf()),
//                code = Code("")
//            )
//        )
//        println(res)
//
//
//        Thread.sleep(1000)
//    }
//
//
//    @PostConstruct
//    fun setup() {
//        sdk = DefaultHamalSdk("http://localhost:${localPort}")
//    }
//
//    private lateinit var sdk: HamalSdk
//}