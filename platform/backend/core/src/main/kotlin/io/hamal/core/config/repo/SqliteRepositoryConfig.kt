package io.hamal.core.config.repo

import io.hamal.core.config.BackendBasePath
import io.hamal.repository.api.*
import io.hamal.repository.memory.MemoryExecLogRepository
import io.hamal.repository.memory.MemoryReqRepository
import io.hamal.repository.sqlite.SqliteAuthRepository
import io.hamal.repository.sqlite.SqliteStateRepository
import io.hamal.repository.sqlite.log.SqliteBroker
import io.hamal.repository.sqlite.log.SqliteBrokerRepository
import io.hamal.repository.sqlite.record.account.SqliteAccountRepository
import io.hamal.repository.sqlite.record.blueprint.SqliteBlueprintRepository
import io.hamal.repository.sqlite.record.code.SqliteCodeRepository
import io.hamal.repository.sqlite.record.exec.SqliteExecRepository
import io.hamal.repository.sqlite.record.extension.SqliteExtensionRepository
import io.hamal.repository.sqlite.record.func.SqliteFuncRepository
import io.hamal.repository.sqlite.record.group.SqliteGroupRepository
import io.hamal.repository.sqlite.record.hook.SqliteHookRepository
import io.hamal.repository.sqlite.record.flow.SqliteFlowRepository
import io.hamal.repository.sqlite.record.trigger.SqliteTriggerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path


@Configuration
@Profile("sqlite")
open class SqliteRepositoryConfig(backendBasePath: BackendBasePath) {

    @Bean
    open fun platformEventBrokerRepository() = SqliteBrokerRepository(SqliteBroker(path.resolve("platform-event")))

    @Bean
    open fun eventBrokerRepository() = SqliteBrokerRepository(SqliteBroker(path.resolve("event")))

    @Bean
    open fun accountRepository() = SqliteAccountRepository(SqliteAccountRepository.Config(path))

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = SqliteAuthRepository(SqliteAuthRepository.Config(path))

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    @Bean
    open fun blueprintRepository() = SqliteBlueprintRepository(SqliteBlueprintRepository.Config(path))

    @Bean
    open fun blueprintCmdRepository(): BlueprintCmdRepository = blueprintRepository()

    @Bean
    open fun blueprintQueryRepository(): BlueprintQueryRepository = blueprintRepository()

    @Bean
    open fun codeRepository() = SqliteCodeRepository(SqliteCodeRepository.Config(path))

    @Bean
    open fun codeCmdRepository() = codeRepository()

    @Bean
    open fun codeQueryRepository() = codeRepository()

    @Bean
    open fun extensionRepository() = SqliteExtensionRepository(SqliteExtensionRepository.Config(path))

    @Bean
    open fun extensionCmdRepository() = extensionRepository()

    @Bean
    open fun extensionQueryRepository() = extensionRepository()

    @Bean
    open fun funcRepository() = SqliteFuncRepository(SqliteFuncRepository.Config(path))

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = funcRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = funcRepository()

    @Bean
    open fun groupRepository() = SqliteGroupRepository(SqliteGroupRepository.Config(path))

    @Bean
    open fun groupQueryRepository() = groupRepository()

    @Bean
    open fun groupCmdRepository() = groupRepository()

    @Bean
    open fun hookRepository() = SqliteHookRepository(SqliteHookRepository.Config(path))

    @Bean
    open fun hookQueryRepository() = hookRepository()

    @Bean
    open fun hookCmdRepository() = hookRepository()

    @Bean
    open fun flowRepository() = SqliteFlowRepository(SqliteFlowRepository.Config(path))

    @Bean
    open fun flowCmdRepository(): FlowCmdRepository = flowRepository()

    @Bean
    open fun flowQueryRepository(): FlowQueryRepository = flowRepository()

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

    private val path = Path(backendBasePath.value)
}