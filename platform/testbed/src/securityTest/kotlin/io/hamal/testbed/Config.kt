package io.hamal.testbed

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryFixedTime
import io.hamal.core.component.SetupInternalTopics
import io.hamal.core.config.BackendBasePath
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.EndpointCmdRepository.CreateCmd
import io.hamal.repository.api.ExecCmdRepository.PlanCmd
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@TestConfiguration
class TestApiConfig {

    @Bean
    fun backendBasePath() = BackendBasePath("/tmp/hamal/testbed/${UUID.randomUUID()}")

    @Bean
    fun delayRetry(): DelayRetry = DelayRetryFixedTime(1.milliseconds)
}

@TestConfiguration
class TestSetupConfig {

    @PostConstruct
    fun setup() {
        try {
            setupInternalTopics()

            setupUser(1) // test user & resources which other users try to access
            setupAnonymous(200)
            setupUser(300)
        } catch (t: Throwable) {
            t.printStackTrace()
            fail("Test setup failed", t)
        }
    }

    private fun setupAnonymous(id: Int) {
        accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(id),
                accountId = AccountId(id),
                accountType = AccountType.Anonymous,
                salt = PasswordSalt("$id-salt")
            )
        )

        authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(id),
                authId = AuthId(id),
                accountId = AccountId(id),
                token = AuthToken("$id-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        )
    }


    private fun setupUser(id: Int) {
        accountRepository.create(
            AccountCmdRepository.CreateCmd(
                id = CmdId(id),
                accountId = AccountId(id),
                accountType = AccountType.User,
                salt = PasswordSalt("$id-salt")
            )
        )

        authRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = CmdId(id),
                authId = AuthId(id),
                accountId = AccountId(id),
                token = AuthToken("$id-token"),
                expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(1, ChronoUnit.DAYS))
            )
        )

        workspaceRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = CmdId(id),
                workspaceId = WorkspaceId(id),
                name = WorkspaceName("$id-workspace"),
                creatorId = AccountId(id)
            )
        )

        namespaceRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = CmdId(id),
                namespaceId = NamespaceId(id),
                workspaceId = WorkspaceId(id),
                name = NamespaceName("$id-namespace")
            )
        )

        namespaceTreeRepository.create(
            NamespaceTreeCmdRepository.CreateCmd(
                id = CmdId(id),
                treeId = NamespaceTreeId(id),
                workspaceId = WorkspaceId(id),
                rootNodeId = NamespaceId(id)
            )
        )

        codeRepository.create(
            CodeCmdRepository.CreateCmd(
                id = CmdId(id),
                codeId = CodeId(id),
                value = CodeValue(""),
                workspaceId = WorkspaceId(id)
            )
        )

        funcRepository.create(
            FuncCmdRepository.CreateCmd(
                id = CmdId(id),
                funcId = FuncId(id),
                name = FuncName("$id-func"),
                workspaceId = WorkspaceId(id),
                namespaceId = NamespaceId(id),
                inputs = FuncInputs(),
                codeId = CodeId(id),
                codeVersion = CodeVersion(1)
            )
        )

        endpointRepository.create(
            CreateCmd(
                id = CmdId(id),
                endpointId = EndpointId(id),
                funcId = FuncId(id),
                workspaceId = WorkspaceId(id),
                namespaceId = NamespaceId(id),
                name = EndpointName("$id-name"),
            )
        )

        execRepository.plan(
            PlanCmd(
                id = CmdId(id),
                execId = ExecId(id),
                namespaceId = NamespaceId(id),
                workspaceId = WorkspaceId(id),
                correlation = Correlation(
                    id = CorrelationId("test"),
                    funcId = FuncId(id)
                ),
                inputs = ExecInputs(),
                code = ExecCode(
                    id = CodeId(id),
                    version = CodeVersion(1)
                ),
                Invocation.Func
            )
        )

        extensionRepository.create(
            ExtensionCmdRepository.CreateCmd(
                id = CmdId(id),
                extensionId = ExtensionId(id),
                name = ExtensionName("$id-extension"),
                code = ExtensionCode(
                    id = CodeId(id),
                    version = CodeVersion(1)
                ),
                workspaceId = WorkspaceId(id)
            )
        )

        hookRepository.create(
            HookCmdRepository.CreateCmd(
                id = CmdId(id),
                hookId = HookId(id),
                name = HookName("$id-hook"),
                namespaceId = NamespaceId(id),
                workspaceId = WorkspaceId(id)
            )
        )

        stateRepository.set(
            StateCmdRepository.SetCmd(
                id = CmdId(id),
                correlatedState = CorrelatedState(
                    correlation = Correlation(
                        id = CorrelationId("correlationId"),
                        funcId = FuncId(id)
                    ),
                    value = State(HotObject.builder().set("value", 1337).build())
                )
            )
        )

        topicRepository.create(
            TopicCmdRepository.TopicCreateCmd(
                id = CmdId(id),
                topicId = TopicId(id),
                name = TopicName("$id-namespace-topic"),
                namespaceId = NamespaceId(id),
                workspaceId = WorkspaceId(id),
                type = TopicType.Namespace,
                logTopicId = LogTopicId(id)
            )
        )

        topicRepository.create(
            TopicCmdRepository.TopicCreateCmd(
                id = CmdId(id + 1),
                topicId = TopicId(id + 1),
                name = TopicName("$id-workspace-topic"),
                namespaceId = NamespaceId(id),
                workspaceId = WorkspaceId(id),
                type = TopicType.Workspace,
                logTopicId = LogTopicId(id + 1)
            )
        )

        topicRepository.create(
            TopicCmdRepository.TopicCreateCmd(
                id = CmdId(id + 2),
                topicId = TopicId(id + 2),
                name = TopicName("$id-public-topic"),
                namespaceId = NamespaceId(id),
                workspaceId = WorkspaceId(id),
                type = TopicType.Public,
                logTopicId = LogTopicId(id + 2)
            )
        )

        triggerRepository.create(
            TriggerCmdRepository.CreateFixedRateCmd(
                id = CmdId(id),
                triggerId = TriggerId(id),
                name = TriggerName("$id-fixed-rate-trigger"),
                funcId = FuncId(id),
                workspaceId = WorkspaceId(id),
                namespaceId = NamespaceId(id),
                inputs = TriggerInputs(),
                status = TriggerStatus.Active,
                duration = TriggerDuration("PT10S")
            )
        )

        triggerRepository.create(
            TriggerCmdRepository.CreateEventCmd(
                id = CmdId(id + 1),
                triggerId = TriggerId(id + 1),
                name = TriggerName("$id-event-trigger"),
                funcId = FuncId(id),
                workspaceId = WorkspaceId(id),
                namespaceId = NamespaceId(id),
                inputs = TriggerInputs(),
                status = TriggerStatus.Active,
                topicId = TopicId(id)
            )
        )

        triggerRepository.create(
            TriggerCmdRepository.CreateHookCmd(
                id = CmdId(id + 2),
                triggerId = TriggerId(id + 2),
                name = TriggerName("$id-hook-trigger"),
                funcId = FuncId(id),
                workspaceId = WorkspaceId(id),
                namespaceId = NamespaceId(id),
                inputs = TriggerInputs(),
                status = TriggerStatus.Inactive,
                hookId = HookId(id),
                hookMethod = HookMethod.Post
            )
        )

        triggerRepository.create(
            TriggerCmdRepository.CreateCronCmd(
                id = CmdId(id + 3),
                triggerId = TriggerId(id + 3),
                name = TriggerName("$id-cron-trigger"),
                funcId = FuncId(id),
                workspaceId = WorkspaceId(id),
                namespaceId = NamespaceId(id),
                inputs = TriggerInputs(),
                status = TriggerStatus.Active,
                cron = CronPattern("0 0 * * * *")
            )
        )

    }

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var authRepository: AuthRepository

    @Autowired
    lateinit var codeRepository: CodeRepository

    @Autowired
    lateinit var endpointRepository: EndpointRepository

    @Autowired
    lateinit var execRepository: ExecRepository

    @Autowired
    lateinit var extensionRepository: ExtensionRepository

    @Autowired
    lateinit var funcRepository: FuncRepository

    @Autowired
    lateinit var hookRepository: HookRepository

    @Autowired
    lateinit var namespaceRepository: NamespaceRepository

    @Autowired
    lateinit var namespaceTreeRepository: NamespaceTreeRepository

    @Autowired
    lateinit var stateRepository: StateRepository

    @Autowired
    lateinit var topicRepository: TopicRepository

    @Autowired
    lateinit var triggerRepository: TriggerRepository

    @Autowired
    lateinit var workspaceRepository: WorkspaceRepository

    @Autowired
    lateinit var setupInternalTopics: SetupInternalTopics

}
