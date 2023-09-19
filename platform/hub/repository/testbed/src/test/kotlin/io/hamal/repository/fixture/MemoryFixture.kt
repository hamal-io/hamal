package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.*
import io.hamal.repository.memory.log.*
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {
    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        BrokerRepository::class -> MemoryBrokerRepository() as REPO
        BrokerConsumersRepository::class -> MemoryBrokerConsumersRepository() as REPO
        BrokerTopicsRepository::class -> MemoryBrokerTopicsRepository() as REPO
        SegmentRepository::class -> MemorySegmentRepository(MemorySegment(Segment.Id(2810), TopicId(1506))) as REPO
        TopicRepository::class -> MemoryTopicRepository(Topic(TopicId(23), GroupId(1), TopicName("test-topic"))) as REPO
        else -> TODO()
    }
}