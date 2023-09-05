package io.hamal.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Event
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.repository.api.*
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.log.BrokerRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer.Random
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.temporal.ChronoUnit.DAYS

@ContextConfiguration(
    classes = [
        ApiConfig::class,
    ]
)
@SpringBootTest(
    webEnvironment = RANDOM_PORT
)
@ActiveProfiles("memory")
@TestMethodOrder(Random::class)
internal abstract class BaseTest {

    @LocalServerPort
    lateinit var localPort: Number

    @Autowired
    lateinit var accountCmdRepository: AccountCmdRepository

    @Autowired
    lateinit var authCmdRepository: AuthCmdRepository

    @Autowired
    lateinit var hubEventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var funcQueryRepository: FuncQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var groupCmdRepository: GroupCmdRepository

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

    lateinit var testAccount: Account
    lateinit var testAuthToken: AuthToken
    lateinit var testGroup: Group

    @BeforeEach
    fun before() {

        accountCmdRepository.clear()
        authCmdRepository.clear()
        hubEventBrokerRepository.clear()
        eventBrokerRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        groupCmdRepository.clear()
        namespaceCmdRepository.clear()
        reqCmdRepository.clear()
        stateCmdRepository.clear()
        triggerCmdRepository.clear()


        testAccount = accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
                name = AccountName("test-root"),
                email = AccountEmail("test@hamal.io"),
                salt = PasswordSalt("test-salt")
            )
        )

        testAuthToken = (authCmdRepository.create(
            CreateTokenAuthCmd(
                id = CmdId(3),
                authId = generateDomainId(::AuthId),
                accountId = testAccount.id,
                token = AuthToken("test-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, DAYS))
            )
        ) as TokenAuth).token

        testGroup = groupCmdRepository.create(
            GroupCmdRepository.CreateCmd(
                id = CmdId(4),
                groupId = generateDomainId(::GroupId),
                name = GroupName("test-group"),
                creatorId = testAccount.id
            )
        )

        namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = generateDomainId(::NamespaceId),
                groupId = testGroup.id,
                name = NamespaceName("hamal"),
                inputs = NamespaceInputs()
            )
        )

    }


    fun createExec(
        execId: ExecId,
        status: ExecStatus,
        correlation: Correlation? = null,
        code: CodeType = CodeType(""),
        events: List<Event> = listOf()
    ): Exec {

        val planedExec = execCmdRepository.plan(
            ExecCmdRepository.PlanCmd(
                id = CmdId(1),
                execId = execId,
                groupId = testGroup.id,
                correlation = correlation,
                inputs = ExecInputs(),
                code = code,
                events = events
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

        val startedExec = execCmdRepository.start(StartCmd(CmdId(4))).first()
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