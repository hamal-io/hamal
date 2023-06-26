package io.hamal.backend.instance.web

import io.hamal.backend.instance.BackendConfig
import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.query.EventQueryService
import io.hamal.backend.instance.service.query.ExecQueryService
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.ReqStatus
import io.hamal.lib.domain.vo.port.GenerateDomainId
import io.hamal.lib.http.HttpTemplate
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        BackendConfig::class,
    ]
)
@SpringBootTest(
    webEnvironment = RANDOM_PORT
)
@ActiveProfiles("memory")
@TestMethodOrder(MethodOrderer.Random::class)
abstract class BaseRouteIT {

    @LocalServerPort
    lateinit var localPort: Number

    @Autowired
    lateinit var eventBrokerRepository: LogBrokerRepository<*>

    @Autowired
    lateinit var eventQueryService: EventQueryService<*>

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execCmdService: ExecCmdService

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var execQueryService: ExecQueryService

    @Autowired
    lateinit var funcQueryService: FuncQueryService

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var reqQueryRepository: ReqQueryRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    val httpTemplate: HttpTemplate by lazy {
        HttpTemplate(
            baseUrl = "http://localhost:${localPort}"
        )
    }

    @BeforeEach
    fun before() {
        eventBrokerRepository.clear()
        reqCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
    }

    fun verifyReqFailed(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Failed))
        }
    }

    fun verifyReqCompleted(id: ReqId) {
        with(reqQueryRepository.find(id)!!) {
            assertThat(id, equalTo(id))
            assertThat(status, equalTo(ReqStatus.Completed))
        }
    }

    fun verifyNoRequests() {
        val requests = reqQueryRepository.list { }
        assertThat(requests, empty())
    }


}