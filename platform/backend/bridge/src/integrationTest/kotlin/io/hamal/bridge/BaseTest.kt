package io.hamal.bridge

import io.hamal.core.CoreConfig
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ExecStates
import io.hamal.lib.domain._enum.ExecStates.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Root
import io.hamal.lib.domain.vo.AuthToken.Companion.AuthToken
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.ExpiresAt.Companion.ExpiresAt
import io.hamal.lib.domain.vo.NamespaceName.Companion.NamespaceName
import io.hamal.lib.domain.vo.PasswordSalt.Companion.PasswordSalt
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.WorkspaceName.Companion.WorkspaceName
import io.hamal.repository.api.*
import io.hamal.repository.api.AuthCmdRepository.CreateExecTokenAuthCmd
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.ExecCmdRepository.StartCmd
import io.hamal.repository.api.log.LogBrokerRepository
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
        BridgeConfig::class,
        CoreConfig::class
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
    lateinit var codeCmdRepository: CodeCmdRepository

    @Autowired
    lateinit var execCmdRepository: ExecCmdRepository

    @Autowired
    lateinit var execQueryRepository: ExecQueryRepository

    @Autowired
    lateinit var funcQueryRepository: FuncQueryRepository

    @Autowired
    lateinit var funcCmdRepository: FuncCmdRepository

    @Autowired
    lateinit var workspaceCmdRepository: WorkspaceCmdRepository

    @Autowired
    lateinit var namespaceQueryRepository: NamespaceQueryRepository

    @Autowired
    lateinit var namespaceCmdRepository: NamespaceCmdRepository

    @Autowired
    lateinit var logBrokerRepository: LogBrokerRepository

    @Autowired
    lateinit var requestQueryRepository: RequestQueryRepository

    @Autowired
    lateinit var requestCmdRepository: RequestCmdRepository

    @Autowired
    lateinit var stateQueryRepository: StateQueryRepository

    @Autowired
    lateinit var stateCmdRepository: StateCmdRepository

    @Autowired
    lateinit var topicCmdRepository: TopicCmdRepository

    @Autowired
    lateinit var topicQueryRepository: TopicQueryRepository

    @Autowired
    lateinit var triggerCmdRepository: TriggerCmdRepository

    @Autowired
    lateinit var triggerQueryRepository: TriggerQueryRepository

    @Autowired
    lateinit var generateDomainId: GenerateDomainId

    lateinit var testAccount: Account
    lateinit var testAuthToken: AuthToken
    lateinit var testWorkspace: Workspace
    lateinit var testNamespace: Namespace

    @BeforeEach
    fun before() {

        accountCmdRepository.clear()
        authCmdRepository.clear()
        codeCmdRepository.clear()
        execCmdRepository.clear()
        funcCmdRepository.clear()
        workspaceCmdRepository.clear()
        namespaceCmdRepository.clear()
        requestCmdRepository.clear()
        stateCmdRepository.clear()
        triggerCmdRepository.clear()

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
                expiresAt = ExpiresAt(TimeUtils.now().plus(1, DAYS))
            )
        ) as Auth.Token).token

        testWorkspace = workspaceCmdRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = CmdId(4),
                workspaceId = generateDomainId(::WorkspaceId),
                name = WorkspaceName("test-workspace"),
                creatorId = testAccount.id
            )
        )

        val namespaceId = generateDomainId(::NamespaceId)
        testNamespace = namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(1),
                namespaceId = namespaceId,
                workspaceId = testWorkspace.id,
                name = NamespaceName("hamal"),
                features = NamespaceFeaturesMap.default
            )
        )
    }


    fun createExec(
        execId: ExecId,
        status: ExecStates,
        correlation: Correlation? = null,
        codeId: CodeId? = null,
        codeVersion: CodeVersion? = null,
        code: ValueCode? = ValueCode("")
    ): Exec {

        val planedExec = execCmdRepository.plan(
            ExecCmdRepository.PlanCmd(
                id = CmdId(1),
                execId = execId,
                triggerId = TriggerId(23),
                namespaceId = testNamespace.id,
                workspaceId = testWorkspace.id,
                correlation = correlation,
                inputs = ExecInputs(),
                code = ExecCode(
                    id = codeId,
                    version = codeVersion,
                    value = code
                )
            )
        )

        authCmdRepository.create(
            CreateExecTokenAuthCmd(
                id = CmdId(1),
                execId = execId,
                authId = AuthId(execId.value),
                token = ExecToken("ExecToken")
            )
        )


        if (status == Planned) {
            return planedExec
        }

        val scheduled = execCmdRepository.schedule(
            ExecCmdRepository.ScheduleCmd(
                id = CmdId(2),
                execId = planedExec.id,
            )
        )

        if (status == Scheduled) {
            return scheduled
        }

        val queued = execCmdRepository.queue(
            ExecCmdRepository.QueueCmd(
                id = CmdId(3),
                execId = scheduled.id
            )
        )
        if (status == Queued) {
            return queued
        }

        val startedExec = execCmdRepository.start(StartCmd(CmdId(4))).first()
        if (status == Started) {
            return startedExec
        }

        return when (status) {
            Completed -> execCmdRepository.complete(
                ExecCmdRepository.CompleteCmd(
                    id = CmdId(5),
                    execId = startedExec.id,
                    result = ExecResult(
                        ValueObject.builder().set("hamal", "Great success").build()
                    ),
                    state = ExecState(
                        ValueObject.builder().set("persisted", "Instate").build()
                    )
                )
            )

            Failed -> execCmdRepository.fail(
                ExecCmdRepository.FailCmd(
                    id = CmdId(5),
                    execId = startedExec.id,
                    result = ExecResult(
                        ValueObject.builder().set("message", "BaseTest.kt").build()
                    )
                )
            )

            else -> TODO()
        }
    }
}