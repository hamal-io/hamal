package io.hamal.backend.instance.config

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.memory.MemoryReqRepository
import io.hamal.backend.repository.memory.MemoryStateRepository
import io.hamal.backend.repository.memory.log.MemoryLogBrokerRepository
import io.hamal.backend.repository.memory.log.MemoryLogTopic
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
    open fun sqliteBrokerRepository() = SqliteLogBrokerRepository(SqliteLogBroker(path))

    @Bean
    open fun systemEventBrokerRepository() = sqliteBrokerRepository()

    @Bean
    open fun eventBrokerRepository() = sqliteBrokerRepository()

    @Bean
    open fun sqliteFuncRepository() = SqliteFuncRepository(SqliteFuncRepository.Config(path))

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = sqliteFuncRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = sqliteFuncRepository()

    @Bean
    open fun sqliteExecRepository() = SqliteExecRepository(SqliteExecRepository.Config(path))

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = sqliteExecRepository()

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = sqliteExecRepository()

    @Bean
    open fun sqliteTriggerRepository() = SqliteTriggerRepository(SqliteTriggerRepository.Config(path))

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = sqliteTriggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = sqliteTriggerRepository()


    @Bean
    open fun sqliteStateRepository() = SqliteStateRepository(path)

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = sqliteStateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = sqliteStateRepository()

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
    open fun systemEventBrokerRepository(): LogBrokerRepository<MemoryLogTopic> {
        return MemoryLogBrokerRepository()
    }

    @Bean
    open fun eventBrokerRepository(): LogBrokerRepository<MemoryLogTopic> {
        return MemoryLogBrokerRepository()
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