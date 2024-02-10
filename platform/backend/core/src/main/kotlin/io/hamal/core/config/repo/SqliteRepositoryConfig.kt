package io.hamal.core.config.repo

import io.hamal.core.config.BackendBasePath
import io.hamal.repository.api.*
import io.hamal.repository.memory.ExecLogMemoryRepository
import io.hamal.repository.memory.record.TopicMemoryRepository
import io.hamal.repository.sqlite.AuthSqliteRepository
import io.hamal.repository.sqlite.RequestSqliteRepository
import io.hamal.repository.sqlite.StateSqliteRepository
import io.hamal.repository.sqlite.log.LogBrokerSqliteRepository
import io.hamal.repository.sqlite.record.account.AccountSqliteRepository
import io.hamal.repository.sqlite.record.blueprint.BlueprintSqliteRepository
import io.hamal.repository.sqlite.record.code.CodeSqliteRepository
import io.hamal.repository.sqlite.record.endpoint.EndpointSqliteRepository
import io.hamal.repository.sqlite.record.exec.ExecSqliteRepository
import io.hamal.repository.sqlite.record.extension.ExtensionSqliteRepository
import io.hamal.repository.sqlite.record.feedback.FeedbackSqliteRepository
import io.hamal.repository.sqlite.record.func.FuncSqliteRepository
import io.hamal.repository.sqlite.record.group.GroupSqliteRepository
import io.hamal.repository.sqlite.record.hook.HookSqliteRepository
import io.hamal.repository.sqlite.record.namespace.NamespaceSqliteRepository
import io.hamal.repository.sqlite.record.trigger.TriggerSqliteRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path


@Configuration
@Profile("sqlite")
open class SqliteRepositoryConfig(backendBasePath: BackendBasePath) {

    @Bean
    open fun accountRepository() = AccountSqliteRepository(path)

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = AuthSqliteRepository(path)

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    @Bean
    open fun blueprintRepository() = BlueprintSqliteRepository(path)

    @Bean
    open fun blueprintCmdRepository(): BlueprintCmdRepository = blueprintRepository()

    @Bean
    open fun blueprintQueryRepository(): BlueprintQueryRepository = blueprintRepository()

    @Bean
    open fun codeRepository() = CodeSqliteRepository(path)

    @Bean
    open fun codeCmdRepository() = codeRepository()

    @Bean
    open fun codeQueryRepository() = codeRepository()

    @Bean
    open fun endpointRepository() = EndpointSqliteRepository(path)

    @Bean
    open fun endpointCmdRepository() = endpointRepository()

    @Bean
    open fun endpointQueryRepository() = endpointRepository()

    @Bean
    open fun extensionRepository() = ExtensionSqliteRepository(path)

    @Bean
    open fun extensionCmdRepository() = extensionRepository()

    @Bean
    open fun extensionQueryRepository() = extensionRepository()

    @Bean
    open fun feedbackRepository() = FeedbackSqliteRepository(path)

    @Bean
    open fun feedbackCmdRepository() = feedbackRepository()

    @Bean
    open fun feedbackQueryRepository() = feedbackRepository()

    @Bean
    open fun funcRepository() = FuncSqliteRepository(path)

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = funcRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = funcRepository()

    @Bean
    open fun groupRepository() = GroupSqliteRepository(path)

    @Bean
    open fun groupQueryRepository() = groupRepository()

    @Bean
    open fun groupCmdRepository() = groupRepository()

    @Bean
    open fun hookRepository() = HookSqliteRepository(path)

    @Bean
    open fun hookQueryRepository() = hookRepository()

    @Bean
    open fun hookCmdRepository() = hookRepository()

    @Bean
    open fun namespaceRepository() = NamespaceSqliteRepository(path)

    @Bean
    open fun namespaceCmdRepository(): NamespaceCmdRepository = namespaceRepository()

    @Bean
    open fun namespaceQueryRepository(): NamespaceQueryRepository = namespaceRepository()

    @Bean
    open fun execRepository() = ExecSqliteRepository(path)

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = execRepository()

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = execRepository()

    @Bean
    open fun execLogRepository(): ExecLogRepository = ExecLogMemoryRepository()

    @Bean
    open fun execLogCmdRepository(): ExecLogCmdRepository = execLogRepository()

    @Bean
    open fun execLogQueryRepository(): ExecLogQueryRepository = execLogRepository()

    @Bean
    open fun logBrokerRepository() = LogBrokerSqliteRepository(path)

    @Bean
    open fun topicRepository(): TopicRepository = TopicMemoryRepository(
        logBrokerRepository()
    )

    @Bean
    open fun topicCmdRepository(): TopicCmdRepository = topicRepository()

    @Bean
    open fun topicQueryRepository(): TopicQueryRepository = topicRepository()

    @Bean
    open fun triggerRepository() = TriggerSqliteRepository(path)

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = triggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = triggerRepository()

    @Bean
    open fun stateRepository() = StateSqliteRepository(path)

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = stateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = stateRepository()

    @Bean
    open fun requestRepository(): RequestRepository = RequestSqliteRepository(path)

    @Bean
    open fun requestCmdRepository(): RequestCmdRepository = requestRepository()

    @Bean
    open fun requestQueryRepository(): RequestQueryRepository = requestRepository()

    private val path = Path(backendBasePath.value)
}