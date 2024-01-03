package io.hamal.api

import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
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
import java.time.temporal.ChronoUnit.DAYS
import java.util.concurrent.atomic.AtomicInteger


@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [
        TestConfig::class,
        ApiConfig::class,
        BridgeConfig::class,
        CoreConfig::class
    ]
)
@ActiveProfiles(value = ["test", "memory"])
@TestMethodOrder(Random::class)
internal abstract class BaseTest {

    @LocalServerPort
    lateinit var localPort: Number

    @Autowired
    lateinit var accountCmdRepository: AccountCmdRepository

    @Autowired
    lateinit var authCmdRepository: AuthCmdRepository

    @Autowired
    lateinit var blueprintQueryRepository: BlueprintQueryRepository

    @Autowired
    lateinit var blueprintCmdRepository: BlueprintCmdRepository

    @Autowired
    lateinit var codeCmdRepository: CodeCmdRepository

    @Autowired
    lateinit var codeQueryRepository: CodeQueryRepository

    @Autowired
    lateinit var platformEventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var eventBrokerRepository: BrokerRepository

    @Autowired
    lateinit var endpointQueryRepository: EndpointQueryRepository

    @Autowired
    lateinit var endpointCmdRepository: EndpointCmdRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var extensionQueryRepository: ExtensionQueryRepository

    @Autowired
    lateinit var extensionCmdRepository: ExtensionCmdRepository

    @Autowired
    lateinit var funcQueryRepository: FuncQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var groupCmdRepository: GroupCmdRepository

    @Autowired
    lateinit var hookQueryRepository: HookQueryRepository

    @Autowired
    lateinit var hookCmdRepository: HookCmdRepository

    @Autowired
    lateinit var flowQueryRepository: FlowQueryRepository

    @Autowired
    lateinit var flowCmdRepository: FlowCmdRepository

    @Autowired
    lateinit var reqQueryRepository: RequestQueryRepository

    @Autowired
    lateinit var reqCmdRepository: RequestCmdRepository

    @Autowired
    lateinit var stateQueryRepository: StateQueryRepository

    @Autowired
    lateinit var stateCmdRepository: StateCmdRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var triggerQueryRepository: TriggerQueryRepository

    @Autowired
    lateinit var generateDomainId: GenerateId

    lateinit var testAccount: Account
    lateinit var testAuthToken: AuthToken
    lateinit var testGroup: Group
    lateinit var testFlow: Flow

    @BeforeEach
    fun before() {

        accountCmdRepository.clear()
        authCmdRepository.clear()
        blueprintCmdRepository.clear()
        codeCmdRepository.clear()
        platformEventBrokerRepository.clear()
        endpointCmdRepository.clear()
        eventBrokerRepository.clear()
        execCmdRepository.clear()
        extensionCmdRepository.clear()
        funcCmdRepository.clear()
        groupCmdRepository.clear()
        hookCmdRepository.clear()
        flowCmdRepository.clear()
        reqCmdRepository.clear()
        stateCmdRepository.clear()
        triggerCmdRepository.clear()


        testAccount = accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = AccountId.root,
                accountType = Root,
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
                groupId = GroupId.root,
                name = GroupName("test-group"),
                creatorId = testAccount.id
            )
        )

        testFlow = flowCmdRepository.create(
            FlowCmdRepository.CreateCmd(
                id = CmdId(1),
                flowId = FlowId.root,
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
        code: CodeValue? = CodeValue(""),
        invocation: Invocation = EmptyInvocation
    ): Exec {

        val planedExec = execCmdRepository.plan(
            ExecCmdRepository.PlanCmd(
                id = CmdId(1),
                execId = execId,
                flowId = testFlow.id,
                groupId = testGroup.id,
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
                    state = ExecState(MapType("state" to StringType("ful")))
                )
            )

            ExecStatus.Failed -> execCmdRepository.fail(
                ExecCmdRepository.FailCmd(
                    id = CmdId(5),
                    execId = startedExec.id,
                    result = ExecResult(MapType("message" to StringType("BaseTest.kt")))
                )
            )

            else -> TODO()
        }
    }

    protected object CmdGen {
        private val atomicCounter = AtomicInteger(1)

        operator fun invoke(): CmdId {
            return CmdId(atomicCounter.incrementAndGet())
        }
    }
}