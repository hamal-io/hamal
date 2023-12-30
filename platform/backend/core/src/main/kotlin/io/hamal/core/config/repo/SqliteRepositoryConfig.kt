package io.hamal.core.config.repo

import io.hamal.core.config.BackendBasePath
import io.hamal.repository.api.*
import io.hamal.repository.memory.ExecLogMemoryRepository
import io.hamal.repository.memory.ReqMemoryRepository
import io.hamal.repository.sqlite.AuthSqliteRepository
import io.hamal.repository.sqlite.StateSqliteRepository
import io.hamal.repository.sqlite.log.BrokerSqlite
import io.hamal.repository.sqlite.log.BrokerSqliteRepository
import io.hamal.repository.sqlite.record.account.AccountSqliteRepository
import io.hamal.repository.sqlite.record.blueprint.BlueprintSqliteRepository
import io.hamal.repository.sqlite.record.code.CodeSqliteRepository
import io.hamal.repository.sqlite.record.endpoint.EndpointSqliteRepository
import io.hamal.repository.sqlite.record.exec.ExecSqliteRepository
import io.hamal.repository.sqlite.record.extension.ExtensionSqliteRepository
import io.hamal.repository.sqlite.record.feedback.FeedbackSqliteRepository
import io.hamal.repository.sqlite.record.flow.FlowSqliteRepository
import io.hamal.repository.sqlite.record.func.FuncSqliteRepository
import io.hamal.repository.sqlite.record.group.GroupSqliteRepository
import io.hamal.repository.sqlite.record.hook.HookSqliteRepository
import io.hamal.repository.sqlite.record.trigger.TriggerSqliteRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path


@Configuration
@Profile("sqlite")
@OptIn(ExperimentalSerializationApi::class)
open class SqliteRepositoryConfig(backendBasePath: BackendBasePath) {

    @Bean
    open fun platformEventBrokerRepository() = BrokerSqliteRepository(BrokerSqlite(path.resolve("platform-event")))

    @Bean
    open fun eventBrokerRepository() = BrokerSqliteRepository(BrokerSqlite(path.resolve("event")))

    @Bean
    open fun accountRepository() = AccountSqliteRepository(AccountSqliteRepository.Config(path))

    @Bean
    open fun accountQueryRepository() = accountRepository()

    @Bean
    open fun accountCmdRepository() = accountRepository()

    @Bean
    open fun authRepository() = AuthSqliteRepository(AuthSqliteRepository.Config(path))

    @Bean
    open fun authQueryRepository() = authRepository()

    @Bean
    open fun authCmdRepository() = authRepository()

    @Bean
    open fun blueprintRepository() = BlueprintSqliteRepository(BlueprintSqliteRepository.Config(path))

    @Bean
    open fun blueprintCmdRepository(): BlueprintCmdRepository = blueprintRepository()

    @Bean
    open fun blueprintQueryRepository(): BlueprintQueryRepository = blueprintRepository()

    @Bean
    open fun codeRepository() = CodeSqliteRepository(CodeSqliteRepository.Config(path))

    @Bean
    open fun codeCmdRepository() = codeRepository()

    @Bean
    open fun codeQueryRepository() = codeRepository()

    @Bean
    open fun endpointRepository() = EndpointSqliteRepository(EndpointSqliteRepository.Config(path))

    @Bean
    open fun endpointCmdRepository() = endpointRepository()

    @Bean
    open fun endpointQueryRepository() = endpointRepository()

    @Bean
    open fun extensionRepository() = ExtensionSqliteRepository(ExtensionSqliteRepository.Config(path))

    @Bean
    open fun extensionCmdRepository() = extensionRepository()

    @Bean
    open fun extensionQueryRepository() = extensionRepository()

    @Bean
    open fun feedbackRepository() = FeedbackSqliteRepository(FeedbackSqliteRepository.Config(path))

    @Bean
    open fun feedbackCmdRepository() = feedbackRepository()

    @Bean
    open fun feedbackQueryRepository() = feedbackRepository()

    @Bean
    open fun funcRepository() = FuncSqliteRepository(FuncSqliteRepository.Config(path))

    @Bean
    open fun funcCmdRepository(): FuncCmdRepository = funcRepository()

    @Bean
    open fun funcQueryRepository(): FuncQueryRepository = funcRepository()

    @Bean
    open fun groupRepository() = GroupSqliteRepository(GroupSqliteRepository.Config(path))

    @Bean
    open fun groupQueryRepository() = groupRepository()

    @Bean
    open fun groupCmdRepository() = groupRepository()

    @Bean
    open fun hookRepository() = HookSqliteRepository(HookSqliteRepository.Config(path))

    @Bean
    open fun hookQueryRepository() = hookRepository()

    @Bean
    open fun hookCmdRepository() = hookRepository()

    @Bean
    open fun flowRepository() = FlowSqliteRepository(FlowSqliteRepository.Config(path))

    @Bean
    open fun flowCmdRepository(): FlowCmdRepository = flowRepository()

    @Bean
    open fun flowQueryRepository(): FlowQueryRepository = flowRepository()

    @Bean
    open fun execRepository() = ExecSqliteRepository(ExecSqliteRepository.Config(path))

    @Bean
    open fun execCmdRepository(): ExecCmdRepository = execRepository()

    @Bean
    open fun execQueryRepository(): ExecQueryRepository = execRepository()

    @Bean
    open fun execLogRepository(): ExecLogRepository = ExecLogMemoryRepository()

    @Bean
    open fun execLogCmdRepository(): ExecLogCmdRepository = execLogRepository()

    @Bean
    open fun execLogQueryRepository(): ExecLogQueryRepository = execLogRepository()

    @Bean
    open fun triggerRepository() = TriggerSqliteRepository(TriggerSqliteRepository.Config(path))

    @Bean
    open fun triggerCmdRepository(): TriggerCmdRepository = triggerRepository()

    @Bean
    open fun triggerQueryRepository(): TriggerQueryRepository = triggerRepository()

    @Bean
    open fun stateRepository() = StateSqliteRepository(StateSqliteRepository.Config(path))

    @Bean
    open fun stateCmdRepository(): StateCmdRepository = stateRepository()

    @Bean
    open fun stateQueryRepository(): StateQueryRepository = stateRepository()

    @Bean
    open fun reqRepository(): ReqRepository = ReqMemoryRepository(ProtoBuf { })

    @Bean
    open fun reqCmdRepository(): ReqCmdRepository = reqRepository()

    @Bean
    open fun reqQueryRepository(): ReqQueryRepository = reqRepository()

    private val path = Path(backendBasePath.value)
}