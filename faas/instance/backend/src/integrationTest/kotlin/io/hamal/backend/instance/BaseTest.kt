package io.hamal.backend.instance

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.AdhocInvocation
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
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
internal abstract class BaseTest {

    @LocalServerPort
    lateinit var localPort: Number

    @Autowired
    lateinit var eventBrokerRepository: LogBrokerRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var funcQueryRepository: FuncQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var namespaceQueryRepository: NamespaceQueryRepository

    @Autowired
    lateinit var namespaceCmdRepository: NamespaceCmdRepository

    @Autowired
    lateinit var reqQueryRepository: ReqQueryRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var stateQueryRepository: StateQueryRepository

    @Autowired
    lateinit var stateCmdRepository: StateCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var triggerQueryRepository: TriggerQueryRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    @BeforeEach
    fun before() {
        eventBrokerRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        namespaceCmdRepository.clear()
        reqCmdRepository.clear()
        stateCmdRepository.clear()
        triggerCmdRepository.clear()
    }


    fun createExec(
        execId: ExecId,
        status: ExecStatus,
        correlation: Correlation? = null,
        code: CodeType = CodeType("")
    ): Exec {

        val planedExec = execCmdRepository.plan(
            ExecCmdRepository.PlanCmd(
                id = CmdId(1),
                execId = execId,
                correlation = correlation,
                inputs = ExecInputs(),
                code = code,
                invocation = AdhocInvocation()
            )
        )

        if (status == ExecStatus.Planned) {
            return planedExec
        }

        val scheduled = execCmdRepository.schedule(
            ExecCmdRepository.ScheduleCmd(
                id = CmdId(2),
                execId = planedExec.id,
            )
        )

        if (status == ExecStatus.Scheduled) {
            return scheduled
        }

        val queued = execCmdRepository.queue(
            ExecCmdRepository.QueueCmd(
                id = CmdId(3),
                execId = scheduled.id
            )
        )
        if (status == ExecStatus.Queued) {
            return queued
        }

        val startedExec = execCmdRepository.start(ExecCmdRepository.StartCmd(CmdId(4))).first()
        if (status == ExecStatus.Started) {
            return startedExec
        }

        return when (status) {
            ExecStatus.Completed -> execCmdRepository.complete(
                ExecCmdRepository.CompleteCmd(
                    id = CmdId(5),
                    execId = startedExec.id
                )
            )

            ExecStatus.Failed -> execCmdRepository.fail(
                ExecCmdRepository.FailCmd(
                    id = CmdId(5),
                    execId = startedExec.id,
                    cause = ErrorType("BaseTest.kt")
                )
            )

            else -> TODO()
        }
    }
}