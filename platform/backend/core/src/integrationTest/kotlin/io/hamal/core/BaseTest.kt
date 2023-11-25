package io.hamal.core

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.*
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.ExecCmdRepository.PlanCmd
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.log.BrokerRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
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
        CoreConfig::class,
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
    lateinit var accountCmdRepository: AccountCmdRepository

    @Autowired
    lateinit var authCmdRepository: AuthCmdRepository

    @Autowired
    lateinit var codeCmdRepository: CodeCmdRepository

    @Autowired
    lateinit var codeQueryRepository: CodeQueryRepository

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var extensionCmdRepository: ExtensionCmdRepository

    @Autowired
    lateinit var extensionQueryRepository: ExtensionQueryRepository

    @Autowired
    lateinit var funcQueryRepository: FuncQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var groupCmdRepository: GroupCmdRepository

    @Autowired
    lateinit var hookRepository: HookRepository

    @Autowired
    lateinit var flowQueryRepository: FlowQueryRepository

    @Autowired
    lateinit var flowCmdRepository: FlowCmdRepository

    @Autowired
    lateinit var platformEventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var reqQueryRepository: ReqQueryRepository

    @Autowired
    lateinit var reqCmdRepository: ReqCmdRepository

    @Autowired
    lateinit var blueprintCmdRepository: BlueprintCmdRepository

    @Autowired
    lateinit var blueprintQueryRepository: BlueprintQueryRepository

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
    lateinit var testFlow: Flow

    @BeforeEach
    fun before() {

        accountCmdRepository.clear()
        authCmdRepository.clear()
        codeCmdRepository.clear()
        eventBrokerRepository.clear()
        execCmdRepository.clear()
        extensionCmdRepository.clear()
        funcCmdRepository.clear()
        flowCmdRepository.clear()
        groupCmdRepository.clear()
        hookRepository.clear()
        platformEventBrokerRepository.clear()
        reqCmdRepository.clear()
        stateCmdRepository.clear()
        triggerCmdRepository.clear()


        testAccount = accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
                accountType = Root,
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

        testFlow = flowCmdRepository.create(
            FlowCmdRepository.CreateCmd(
                id = CmdId(1),
                flowId = generateDomainId(::FlowId),
                groupId = testGroup.id,
                name = FlowName("hamal"),
                inputs = FlowInputs()
            )
        )

    }


    fun createExec(
        execId: ExecId,
        status: ExecStatus,
        correlation: Correlation? = null,
        codeId: CodeId? = null,
        codeVersion: CodeVersion? = null,
        code: CodeValue = CodeValue(""),
        invocation: Invocation = EmptyInvocation
    ): Exec {

        val planedExec = execCmdRepository.plan(
            PlanCmd(
                id = CmdId(1),
                execId = execId,
                groupId = testGroup.id,
                flowId = testFlow.id,
                correlation = correlation,
                inputs = ExecInputs(),
                code = ExecCode(
                    id = codeId,
                    version = codeVersion,
                    value = code
                ),
                invocation = invocation
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
                    execId = startedExec.id,
                    result = ExecResult(MapType("hamal" to StringType("rocks"))),
                    state = ExecState(MapType("state" to NumberType(23.23)))
                )
            )

            ExecStatus.Failed -> execCmdRepository.fail(
                ExecCmdRepository.FailCmd(
                    id = CmdId(5),
                    execId = startedExec.id,
                    result = ExecResult(
                        MapType("message" to StringType("BaseTest.kt"))
                    )
                )
            )

            else -> TODO()
        }
    }
}