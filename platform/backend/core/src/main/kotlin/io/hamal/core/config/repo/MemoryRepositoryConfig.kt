package io.hamal.core.config.repo

import io.hamal.repository.api.*
import io.hamal.repository.memory.AuthMemoryRepository
import io.hamal.repository.memory.ExecLogMemoryRepository
import io.hamal.repository.memory.RequestMemoryRepository
import io.hamal.repository.memory.StateMemoryRepository
import io.hamal.repository.memory.log.LogBrokerMemoryRepository
import io.hamal.repository.memory.record.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("memory")
open class MemoryRepositoryConfig {

    @Bean
    open fun accountRepository() = AccountMemoryRepository()

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = AuthMemoryRepository()

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    @Bean
    open fun blueprintRepository() = BlueprintMemoryRepository()

    @Bean
    open fun blueprintCmdRepository(): BlueprintCmdRepository = blueprintRepository()

    @Bean
    open fun blueprintQueryRepository(): BlueprintQueryRepository = blueprintRepository()

    @Bean
    open fun codeRepository() = CodeMemoryRepository()

    @Bean
    open fun codeCmdRepository() = codeRepository()

    @Bean
    open fun codeQueryRepository() = codeRepository()

    @Bean
    open fun endpointRepository() = EndpointMemoryRepository()

    @Bean
    open fun endpointCmdRepository() = endpointRepository()

    @Bean
    open fun endpointQueryRepository() = endpointRepository()

    @Bean
    open fun extensionRepository() = ExtensionMemoryRepository()

    @Bean
    open fun extensionCmdRepository() = extensionRepository()

    @Bean
    open fun extensionQueryRepository() = extensionRepository()

    @Bean
    open fun feedbackRepository(): FeedbackRepository = FeedbackMemoryRepository()

    @Bean
    open fun feedbackCmdRepository(): FeedbackCmdRepository = feedbackRepository()

    @Bean
    open fun feedbackQueryRepository(): FeedbackQueryRepository = feedbackRepository()

    @Bean
    open fun funcRepository(): FuncRepository = FuncMemoryRepository()

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = funcRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = funcRepository()

    @Bean
    open fun workspaceRepository() = MemoryWorkspaceRepository()

    @Bean
    open fun workspaceQueryRepository() = workspaceRepository()

    @Bean
    open fun workspaceCmdRepository() = workspaceRepository()

    @Bean
    open fun hookRepository() = HookMemoryRepository()

    @Bean
    open fun hookQueryRepository() = hookRepository()

    @Bean
    open fun hookCmdRepository() = hookRepository()

    @Bean
    open fun namespaceRepository() = NamespaceMemoryRepository()

    @Bean
    open fun namespaceCmdRepository(): NamespaceCmdRepository = namespaceRepository()

    @Bean
    open fun namespaceQueryRepository(): NamespaceQueryRepository = namespaceRepository()

    @Bean
    open fun execRepository(): ExecRepository = ExecMemoryRepository()

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
    open fun logBrokerRepository() = LogBrokerMemoryRepository()

    @Bean
    open fun requestRepository(): RequestRepository = RequestMemoryRepository()

    @Bean
    open fun requestCmdRepository(): RequestCmdRepository = requestRepository()

    @Bean
    open fun requestQueryRepository(): RequestQueryRepository = requestRepository()

    @Bean
    open fun stateRepository(): StateRepository = StateMemoryRepository()

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = stateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = stateRepository()

    @Bean
    open fun topicRepository(): TopicRepository = TopicMemoryRepository(
        logBrokerRepository()
    )

    @Bean
    open fun topicCmdRepository(): TopicCmdRepository = topicRepository()

    @Bean
    open fun topicQueryRepository(): TopicQueryRepository = topicRepository()

    @Bean
    open fun triggerRepository(): TriggerRepository = TriggerMemoryRepository()

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = triggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = triggerRepository()

}