package io.hamal.core.config.repo

import io.hamal.core.config.BackendBasePath
import io.hamal.repository.api.*
import io.hamal.repository.memory.MemoryAuthRepository
import io.hamal.repository.memory.MemoryExecLogRepository
import io.hamal.repository.memory.MemoryMetricRepository
import io.hamal.repository.memory.MemoryReqRepository
import io.hamal.repository.memory.record.MemoryGroupRepository
import io.hamal.repository.sqlite.SqliteStateRepository
import io.hamal.repository.sqlite.log.SqliteBroker
import io.hamal.repository.sqlite.log.SqliteBrokerRepository
import io.hamal.repository.sqlite.record.account.SqliteAccountRepository
import io.hamal.repository.sqlite.record.exec.SqliteExecRepository
import io.hamal.repository.sqlite.record.func.SqliteFuncRepository
import io.hamal.repository.sqlite.record.namespace.SqliteNamespaceRepository
import io.hamal.repository.sqlite.record.trigger.SqliteTriggerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path


@Configuration
@Profile("sqlite")
open class SqliteRepositoryConfig(
    val hubBasePath: BackendBasePath
) {

    @Bean
    open fun platformEventBrokerRepository() = SqliteBrokerRepository(SqliteBroker(path.resolve("hub-event")))

    @Bean
    open fun eventBrokerRepository() = SqliteBrokerRepository(SqliteBroker(path.resolve("event")))

    @Bean
    open fun accountRepository() = SqliteAccountRepository(SqliteAccountRepository.Config(path))

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
    open fun funcRepository() = SqliteFuncRepository(SqliteFuncRepository.Config(path))

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
    open fun namespaceRepository() = SqliteNamespaceRepository(SqliteNamespaceRepository.Config(path))

    @Bean
    open fun namespaceCmdRepository(): NamespaceCmdRepository = namespaceRepository()

    @Bean
    open fun namespaceQueryRepository(): NamespaceQueryRepository = namespaceRepository()

    @Bean
    open fun execRepository() = SqliteExecRepository(SqliteExecRepository.Config(path))

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
    open fun triggerRepository() = SqliteTriggerRepository(SqliteTriggerRepository.Config(path))

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = triggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = triggerRepository()

    @Bean
    open fun stateRepository() = SqliteStateRepository(SqliteStateRepository.Config(path))

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = stateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = stateRepository()

    @Bean
    open fun reqRepository(): ReqRepository = MemoryReqRepository()

    @Bean
    open fun reqCmdRepository(): ReqCmdRepository = reqRepository()

    @Bean
    open fun reqQueryRepository(): ReqQueryRepository = reqRepository()

    @Bean
    open fun metricRepository(): MetricRepository = MemoryMetricRepository()

    private val path = Path(hubBasePath.value)
}