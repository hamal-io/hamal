package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.*
import io.hamal.repository.api.log.*
import io.hamal.repository.memory.MemoryAuthRepository
import io.hamal.repository.memory.MemoryExecLogRepository
import io.hamal.repository.memory.MemoryReqRepository
import io.hamal.repository.memory.MemoryStateRepository
import io.hamal.repository.memory.log.*
import io.hamal.repository.memory.record.*
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {
    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> MemoryAccountRepository() as REPO
        AuthRepository::class -> MemoryAuthRepository() as REPO
        BrokerRepository::class -> MemoryBrokerRepository() as REPO
        BrokerConsumersRepository::class -> MemoryBrokerConsumersRepository() as REPO
        BrokerTopicsRepository::class -> MemoryBrokerTopicsRepository() as REPO
        CodeRepository::class -> MemoryCodeRepository() as REPO
        ExecLogRepository::class -> MemoryExecLogRepository() as REPO
        ExecRepository::class -> MemoryExecRepository() as REPO
        FuncRepository::class -> MemoryFuncRepository() as REPO
        GroupRepository::class -> MemoryGroupRepository() as REPO
        HookRepository::class -> MemoryHookRepository() as REPO
        NamespaceRepository::class -> MemoryNamespaceRepository() as REPO
        ReqRepository::class -> MemoryReqRepository() as REPO
        SegmentRepository::class -> MemorySegmentRepository(MemorySegment(Segment.Id(2810), TopicId(1506))) as REPO
        SnippetRepository::class -> MemorySnippetRepository() as REPO
        StateRepository::class -> MemoryStateRepository() as REPO
        TopicRepository::class -> MemoryTopicRepository(
            Topic(
                TopicId(23),
                NamespaceId(23),
                GroupId(1),
                TopicName("test-topic")
            )
        ) as REPO

        TriggerRepository::class -> MemoryTriggerRepository() as REPO
        else -> TODO()
    }
}