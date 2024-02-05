package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.new_log.LogBrokerRepository
import io.hamal.repository.api.new_log.LogSegmentId
import io.hamal.repository.api.new_log.LogSegmentRepository
import io.hamal.repository.api.new_log.LogTopicRepository
import io.hamal.repository.memory.AuthMemoryRepository
import io.hamal.repository.memory.ExecLogMemoryRepository
import io.hamal.repository.memory.ReqMemoryRepository
import io.hamal.repository.memory.StateMemoryRepository
import io.hamal.repository.memory.new_log.*
import io.hamal.repository.memory.record.*
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {
    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> AccountMemoryRepository() as REPO
        AuthRepository::class -> AuthMemoryRepository() as REPO
        BlueprintRepository::class -> BlueprintMemoryRepository() as REPO
        CodeRepository::class -> CodeMemoryRepository() as REPO
        EndpointRepository::class -> EndpointMemoryRepository() as REPO
        ExecLogRepository::class -> ExecLogMemoryRepository() as REPO
        ExecRepository::class -> ExecMemoryRepository() as REPO
        ExtensionRepository::class -> ExtensionMemoryRepository() as REPO
        FeedbackRepository::class -> FeedbackMemoryRepository() as REPO
        FuncRepository::class -> FuncMemoryRepository() as REPO
        GroupRepository::class -> MemoryGroupRepository() as REPO
        HookRepository::class -> HookMemoryRepository() as REPO
        FlowRepository::class -> FlowMemoryRepository() as REPO
        RequestRepository::class -> ReqMemoryRepository() as REPO

        LogBrokerRepository::class -> LogBrokerMemoryRepository() as REPO
        LogSegmentRepository::class -> LogSegmentMemoryRepository(
            LogSegmentMemory(
                LogSegmentId(2810),
                LogTopicId(1506)
            )
        ) as REPO

        LogTopicRepository::class -> LogTopicMemoryRepository(
            LogTopicMemory(LogTopicId(23))
        ) as REPO

        StateRepository::class -> StateMemoryRepository() as REPO
        TopicRepository::class -> TopicMemoryRepository(LogBrokerMemoryRepository()) as REPO
        TriggerRepository::class -> TriggerMemoryRepository() as REPO
        else -> TODO()
    }
}