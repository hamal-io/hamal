package io.hamal.backend.config

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.memory.MemoryExecRepository
import io.hamal.backend.repository.memory.MemoryFuncRepository
import io.hamal.backend.repository.memory.MemoryStateRepository
import io.hamal.backend.repository.memory.MemoryTriggerRepository
import io.hamal.backend.repository.sqlite.log.DefaultLogBrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
open class RepositoryConfig {
    @Bean
    open fun brokerRepository(): LogBrokerRepository {
        return DefaultLogBrokerRepository(LogBroker(LogBroker.Id(1), Path("/tmp/hamal")))
    }

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = MemoryFuncRepository

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = MemoryFuncRepository

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = MemoryExecRepository

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = MemoryExecRepository

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = MemoryTriggerRepository

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = MemoryTriggerRepository

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = MemoryStateRepository

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = MemoryStateRepository
}