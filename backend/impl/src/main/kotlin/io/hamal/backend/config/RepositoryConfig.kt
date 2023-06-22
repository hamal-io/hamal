package io.hamal.backend.config

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.backend.repository.memory.*
import io.hamal.backend.repository.memory.record.MemoryExecRepository
import io.hamal.backend.repository.memory.record.MemoryFuncRepository
import io.hamal.backend.repository.memory.record.MemoryTriggerRepository
import io.hamal.backend.repository.sqlite.log.SqliteLogBroker
import io.hamal.backend.repository.sqlite.log.SqliteLogBrokerRepository
import io.hamal.backend.repository.sqlite.record.exec.SqliteExecRepository
import io.hamal.backend.repository.sqlite.record.func.SqliteFuncRepository
import io.hamal.backend.repository.sqlite.record.trigger.SqliteTriggerRepository
import io.hamal.lib.common.Shard
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path


@Profile("sqlite")
@Configuration
open class SqliteRepositoryConfig {
    @Bean
    open fun brokerRepository(): SqliteLogBrokerRepository {
        return SqliteLogBrokerRepository(SqliteLogBroker(LogBroker.Id(1), Path("/tmp/hamal")))
    }

    @Bean
    open fun sqliteFuncRepository() =
        SqliteFuncRepository(
            SqliteFuncRepository.Config(
                path = Path("/tmp/hamal"),
                shard = Shard(1)
            )
        )

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = sqliteFuncRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = sqliteFuncRepository()


    @Bean
    open fun sqliteExecRepository() =
        SqliteExecRepository(
            SqliteExecRepository.Config(
                path = Path("/tmp/hamal"),
                shard = Shard(1)
            )
        )

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = sqliteExecRepository()

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = sqliteExecRepository()


    @Bean
    open fun sqliteTriggerRepository() =
        SqliteTriggerRepository(
            SqliteTriggerRepository.Config(
                path = Path("/tmp/hamal"),
                shard = Shard(1)
            )
        )


    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = sqliteTriggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = sqliteTriggerRepository()

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = MemoryStateRepository

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = MemoryStateRepository

    @Bean
    open fun reqCmdRepository(): ReqCmdRepository = MemoryReqRepository

    @Bean
    open fun reqQueryRepository(): ReqQueryRepository = MemoryReqRepository
}

@Profile("memory")
@Configuration
open class MemoryRepositoryConfig {
    @Bean
    //FIXME have a memory impl
    open fun brokerRepository(): SqliteLogBrokerRepository {
        return SqliteLogBrokerRepository(SqliteLogBroker(LogBroker.Id(1), Path("/tmp/hamal")))
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