package io.hamal.backend.instance.config

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.memory.MemoryExecLogRepository
import io.hamal.backend.repository.memory.MemoryReqRepository
import io.hamal.backend.repository.memory.MemoryStateRepository
import io.hamal.backend.repository.memory.log.MemoryLogBrokerRepository
import io.hamal.backend.repository.memory.record.MemoryExecRepository
import io.hamal.backend.repository.memory.record.MemoryFuncRepository
import io.hamal.backend.repository.memory.record.MemoryTriggerRepository
import io.hamal.backend.repository.sqlite.SqliteStateRepository
import io.hamal.backend.repository.sqlite.log.SqliteLogBroker
import io.hamal.backend.repository.sqlite.log.SqliteLogBrokerRepository
import io.hamal.backend.repository.sqlite.record.exec.SqliteExecRepository
import io.hamal.backend.repository.sqlite.record.func.SqliteFuncRepository
import io.hamal.backend.repository.sqlite.record.trigger.SqliteTriggerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path


@Profile("sqlite")
@Configuration
open class SqliteRepositoryConfig {

    @Bean
    open fun systemEventBrokerRepository() = SqliteLogBrokerRepository(SqliteLogBroker(path.resolve("system-event")))

    @Bean
    open fun eventBrokerRepository() = SqliteLogBrokerRepository(SqliteLogBroker(path.resolve("event")))

    @Bean
    open fun funcRepository() = SqliteFuncRepository(SqliteFuncRepository.Config(path))

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = funcRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = funcRepository()

    @Bean
    open fun execRepository() = SqliteExecRepository(SqliteExecRepository.Config(path))

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = execRepository()

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = execRepository()

    @Bean
    open fun execLogCmdRepository(): ExecLogCmdRepository = MemoryExecLogRepository

    @Bean
    open fun execLogQueryRepository(): ExecLogQueryRepository = MemoryExecLogRepository

    @Bean
    open fun triggerRepository() = SqliteTriggerRepository(SqliteTriggerRepository.Config(path))

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = triggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = triggerRepository()

    @Bean
    open fun stateRepository() = SqliteStateRepository(path)

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = stateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = stateRepository()

    @Bean
    open fun reqCmdRepository(): ReqCmdRepository = MemoryReqRepository

    @Bean
    open fun reqQueryRepository(): ReqQueryRepository = MemoryReqRepository

    private val path = Path("/tmp/hamal/faas")
}

@Profile("memory")
@Configuration
open class MemoryRepositoryConfig {

    @Bean
    open fun systemEventBrokerRepository() = MemoryLogBrokerRepository()

    @Bean
    open fun eventBrokerRepository() = MemoryLogBrokerRepository()

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = MemoryFuncRepository

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = MemoryFuncRepository

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
}