package io.hamal.backend.infra.config

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.memory.MemoryExecRepository
import io.hamal.backend.repository.memory.MemoryFuncRepository
import io.hamal.backend.repository.memory.MemoryTriggerRepository
import io.hamal.backend.repository.sqlite.log.DefaultBrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
open class RepositoryConfig {
    @Bean
    open fun brokerRepository(): BrokerRepository {
        return DefaultBrokerRepository(Broker(Broker.Id(1), Path("/tmp/hamal")))
    }

    @Bean
    open fun funcRequestRepository(): FuncRequestRepository = MemoryFuncRepository

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = MemoryFuncRepository

    @Bean
    open fun execRequestRepository(): ExecRequestRepository = MemoryExecRepository

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = MemoryExecRepository

    @Bean
    open fun triggerRequestRepository(): TriggerRequestRepository = MemoryTriggerRepository

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = MemoryTriggerRepository
}