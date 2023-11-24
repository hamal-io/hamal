package io.hamal.core.config.repo

import io.hamal.repository.api.*
import io.hamal.repository.memory.MemoryAuthRepository
import io.hamal.repository.memory.MemoryExecLogRepository
import io.hamal.repository.memory.MemoryReqRepository
import io.hamal.repository.memory.MemoryStateRepository
import io.hamal.repository.memory.log.MemoryBrokerRepository
import io.hamal.repository.memory.record.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("memory")
open class MemoryRepositoryConfig {

    @Bean
    open fun platformEventBrokerRepository() = MemoryBrokerRepository()

    @Bean
    open fun eventBrokerRepository() = MemoryBrokerRepository()

    @Bean
    open fun accountRepository() = MemoryAccountRepository()

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = MemoryAuthRepository()

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    @Bean
    open fun blueprintRepository() = MemoryBlueprintRepository()

    @Bean
    open fun blueprintCmdRepository(): BlueprintCmdRepository = blueprintRepository()

    @Bean
    open fun blueprintQueryRepository(): BlueprintQueryRepository = blueprintRepository()

    @Bean
    open fun codeRepository() = MemoryCodeRepository()

    @Bean
    open fun codeCmdRepository() = codeRepository()

    @Bean
    open fun codeQueryRepository() = codeRepository()

    @Bean
    open fun endpointRepository() = MemoryEndpointRepository()

    @Bean
    open fun endpointCmdRepository() = endpointRepository()

    @Bean
    open fun endpointQueryRepository() = endpointRepository()

    @Bean
    open fun extensionRepository() = MemoryExtensionRepository()

    @Bean
    open fun extensionCmdRepository() = extensionRepository()

    @Bean
    open fun extensionQueryRepository() = extensionRepository()

    @Bean
    open fun funcRepository(): FuncRepository = MemoryFuncRepository()

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = funcRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = funcRepository()

    @Bean
    open fun groupRepository() = MemoryGroupRepository()

    @Bean
    open fun groupQueryRepository() = groupRepository()

    @Bean
    open fun groupCmdRepository() = groupRepository()

    @Bean
    open fun hookRepository() = MemoryHookRepository()

    @Bean
    open fun hookQueryRepository() = hookRepository()

    @Bean
    open fun hookCmdRepository() = hookRepository()

    @Bean
    open fun flowRepository() = MemoryFlowRepository()

    @Bean
    open fun flowCmdRepository(): FlowCmdRepository = flowRepository()

    @Bean
    open fun flowQueryRepository(): FlowQueryRepository = flowRepository()

    @Bean
    open fun execRepository(): ExecRepository = MemoryExecRepository()

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = execRepository()

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = execRepository()

    @Bean
    open fun execLogRepository(): ExecLogRepository = MemoryExecLogRepository()

    @Bean
    open fun execLogCmdRepository(): ExecLogCmdRepository = execLogRepository()

    @Bean
    open fun execLogQueryRepository(): ExecLogQueryRepository = execLogRepository()

    @Bean
    open fun reqRepository(): ReqRepository = MemoryReqRepository()

    @Bean
    open fun reqCmdRepository(): ReqCmdRepository = reqRepository()

    @Bean
    open fun reqQueryRepository(): ReqQueryRepository = reqRepository()

    @Bean
    open fun stateRepository(): StateRepository = MemoryStateRepository()

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = stateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = stateRepository()

    @Bean
    open fun triggerRepository(): TriggerRepository = MemoryTriggerRepository()

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = triggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = triggerRepository()

}