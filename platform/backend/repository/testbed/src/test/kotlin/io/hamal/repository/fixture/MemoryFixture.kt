package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.*
import io.hamal.repository.api.log.*
import io.hamal.repository.memory.AuthMemoryRepository
import io.hamal.repository.memory.ExecLogMemoryRepository
import io.hamal.repository.memory.ReqMemoryRepository
import io.hamal.repository.memory.StateMemoryRepository
import io.hamal.repository.memory.log.*
import io.hamal.repository.memory.record.*
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {
    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> AccountMemoryRepository() as REPO
        AuthRepository::class -> AuthMemoryRepository() as REPO
        BlueprintRepository::class -> BlueprintMemoryRepository() as REPO
        BrokerRepository::class -> BrokerMemoryRepository() as REPO
        BrokerConsumersRepository::class -> BrokerConsumersMemoryRepository() as REPO
        BrokerTopicsRepository::class -> BrokerTopicsMemoryRepository() as REPO
        CodeRepository::class -> CodeMemoryRepository() as REPO
        EndpointRepository::class -> EndpointMemoryRepository() as REPO
        ExecLogRepository::class -> ExecLogMemoryRepository() as REPO
        ExecRepository::class -> ExecMemoryRepository() as REPO
        ExtensionRepository::class -> ExtensionMemoryRepository() as REPO
        FuncRepository::class -> FuncMemoryRepository() as REPO
        GroupRepository::class -> MemoryGroupRepository() as REPO
        HookRepository::class -> HookMemoryRepository() as REPO
        FlowRepository::class -> FlowMemoryRepository() as REPO
        ReqRepository::class -> ReqMemoryRepository() as REPO
        SegmentRepository::class -> SegmentMemoryRepository(SegmentMemory(Segment.Id(2810), TopicId(1506))) as REPO
        StateRepository::class -> StateMemoryRepository() as REPO
        TopicRepository::class -> TopicMemoryRepository(
            Topic(
                TopicId(23),
                FlowId(23),
                GroupId(1),
                TopicName("test-topic")
            )
        ) as REPO

        TriggerRepository::class -> TriggerMemoryRepository() as REPO
        else -> TODO()
    }
}