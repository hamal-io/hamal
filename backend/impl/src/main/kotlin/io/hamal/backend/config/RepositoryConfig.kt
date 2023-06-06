package io.hamal.backend.config

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.memory.MemoryReqRepository
import io.hamal.backend.repository.memory.MemoryStateRepository
import io.hamal.backend.repository.sqlite.log.DefaultLogBrokerRepository
import io.hamal.backend.repository.sqlite.record.exec.SqliteExecRepository
import io.hamal.backend.repository.sqlite.record.func.SqliteFuncRepository
import io.hamal.backend.repository.sqlite.record.trigger.SqliteTriggerRepository
import io.hamal.lib.common.Shard
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