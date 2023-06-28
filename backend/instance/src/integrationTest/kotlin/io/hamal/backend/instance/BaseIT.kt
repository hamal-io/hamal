package io.hamal.backend.instance

import io.hamal.backend.instance.service.cmd.ExecCmdService
import io.hamal.backend.instance.service.query.EventQueryService
import io.hamal.backend.instance.service.query.ExecQueryService
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.backend.instance.service.query.TriggerQueryService
import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.memory.log.MemoryLogTopic
import io.hamal.lib.domain.vo.port.GenerateDomainId
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

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
internal abstract class BaseIT {

    @LocalServerPort
    lateinit var localPort: Number

    @Autowired
    lateinit var eventBrokerRepository: LogBrokerRepository<MemoryLogTopic>

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
    lateinit var funcQueryRepository: FuncQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var reqQueryRepository: ReqQueryRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var triggerQueryService: TriggerQueryService

    @Autowired
    lateinit var triggerQueryRepository: TriggerQueryRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    @BeforeEach
    fun before() {
        eventBrokerRepository.clear()
        reqCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        triggerCmdRepository.clear()
    }
}