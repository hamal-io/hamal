package io.hamal.bootstrap

import io.hamal.agent.AgentConfig
import io.hamal.backend.instance.BackendConfig
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.FuncCmdRepository
import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.bootstrap.suite.func.functionTests
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HamalSdk
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        BackendConfig::class,
        AgentConfig::class
    ]
)
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    properties = ["server.port=8084"]
)
abstract class BaseHamalTest {

    @LocalServerPort
    lateinit var localPort: Number

    @Autowired
    lateinit var eventBrokerRepository: LogBrokerRepository<*>

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @TestFactory
    fun run(): List<DynamicTest> =
        functionTests(TestContext(sdk))

    @BeforeEach
    fun before() {
        eventBrokerRepository.clear()
        reqCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        triggerCmdRepository.clear()
    }


    @PostConstruct
    fun setup() {
        sdk = DefaultHamalSdk("http://localhost:${localPort}")
    }

    private lateinit var sdk: HamalSdk

}

data class TestContext(
    val sdk: HamalSdk,
) {


    fun createTopic(name: TopicName): TopicId {
        // FIXME replace with sdk
        return HttpTemplate("http://localhost:8084")
            .post("/v1/topics")
            .body(CreateTopicReq(name))
            .execute(SubmittedCreateTopicReq::class)
            .topicId
    }
}