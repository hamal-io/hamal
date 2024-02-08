package io.hamal.core

import io.hamal.core.component.SetupInternalTopics
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.repository.api.*
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.ExecCmdRepository.PlanCmd
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.log.LogBrokerRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import java.time.temporal.ChronoUnit.DAYS

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [CoreConfig::class]
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
    lateinit var endpointRepository: EndpointRepository

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
    lateinit var namespaceQueryRepository: NamespaceQueryRepository

    @Autowired
    lateinit var namespaceCmdRepository: NamespaceCmdRepository

    @Autowired
    lateinit var reqQueryRepository: RequestQueryRepository

    @Autowired
    lateinit var reqCmdRepository: RequestCmdRepository

    @Autowired
    lateinit var blueprintCmdRepository: BlueprintCmdRepository

    @Autowired
    lateinit var blueprintQueryRepository: BlueprintQueryRepository

    @Autowired
    lateinit var stateQueryRepository: StateQueryRepository

    @Autowired
    lateinit var stateCmdRepository: StateCmdRepository

    @Autowired
    lateinit var topicCmdRepository: TopicCmdRepository

    @Autowired
    lateinit var topicQueryRepository: TopicQueryRepository

    @Autowired
    lateinit var logBrokerRepository: LogBrokerRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var triggerQueryRepository: TriggerQueryRepository

    @Autowired
    lateinit var generateDomainId: GenerateId

    @Autowired
    lateinit var setupInternalTopics: SetupInternalTopics

    lateinit var testAccount: Account
    lateinit var testAuthToken: AuthToken
    lateinit var testGroup: Group
    lateinit var testNamespace: Namespace

    @BeforeEach
    fun before() {

        accountCmdRepository.clear()
        authCmdRepository.clear()
        codeCmdRepository.clear()
        execCmdRepository.clear()
        extensionCmdRepository.clear()
        funcCmdRepository.clear()
        namespaceCmdRepository.clear()
        groupCmdRepository.clear()
        hookRepository.clear()
        reqCmdRepository.clear()
        stateCmdRepository.clear()
        topicCmdRepository.clear()
        logBrokerRepository.clear()
        triggerCmdRepository.clear()

        setupInternalTopics()

        testAccount = accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(2),
                accountId = generateDomainId(::AccountId),
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
                groupId = generateDomainId(::GroupId),
                name = GroupName("test-group"),
                creatorId = testAccount.id
            )
        )

        testNamespace = namespaceCmdRepository.create(
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
                namespaceId = testNamespace.id,
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
                    result = ExecResult(HotObject.builder().set("hamal", "rocks").build()),
                    state = ExecState(HotObject.builder().set("state", 23.23).build())
                )
            )

            ExecStatus.Failed -> execCmdRepository.fail(
                ExecCmdRepository.FailCmd(
                    id = CmdId(5),
                    execId = startedExec.id,
                    result = ExecResult(HotObject.builder().set("message", "BaseTest.kt").build())
                )
            )

            else -> TODO()
        }
    }
}