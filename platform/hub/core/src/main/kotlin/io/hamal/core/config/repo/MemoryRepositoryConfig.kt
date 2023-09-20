package io.hamal.core.config.repo

import io.hamal.repository.api.*
import io.hamal.repository.memory.*
import io.hamal.repository.memory.log.MemoryBrokerRepository
import io.hamal.repository.memory.record.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("memory")
open class MemoryRepositoryConfig {

    @Bean
    open fun hubEventBrokerRepository() = MemoryBrokerRepository()

    @Bean
    open fun eventBrokerRepository() = MemoryBrokerRepository()

    @Bean
    open fun accountRepository() = MemoryAccountRepository

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = MemoryAuthRepository

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = MemoryFuncRepository

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = MemoryFuncRepository

    @Bean
    open fun groupRepository() = MemoryGroupRepository

    @Bean
    open fun groupQueryRepository() = groupRepository()

    @Bean
    open fun groupCmdRepository() = groupRepository()

    @Bean
    open fun namespaceCmdRepository(): NamespaceCmdRepository = MemoryNamespaceRepository

    @Bean
    open fun namespaceQueryRepository(): NamespaceQueryRepository = MemoryNamespaceRepository

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = MemoryExecRepository

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = MemoryExecRepository

    @Bean
    open fun execLogCmdRepository(): ExecLogCmdRepository = MemoryExecLogRepository

    @Bean
    open fun execLogQueryRepository(): ExecLogQueryRepository = MemoryExecLogRepository

    @Bean
    open fun reqCmdRepository(): ReqCmdRepository = MemoryReqRepository

    @Bean
    open fun reqQueryRepository(): ReqQueryRepository = MemoryReqRepository

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = MemoryStateRepository

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = MemoryStateRepository

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = MemoryTriggerRepository

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = MemoryTriggerRepository

    @Bean
    open fun metricRepository(): MetricRepository = memoryMetricRepository
}