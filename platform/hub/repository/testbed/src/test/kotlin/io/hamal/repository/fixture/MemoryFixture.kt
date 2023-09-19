package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.log.TopicRepository
import io.hamal.repository.memory.log.MemoryBrokerTopicsRepository
import io.hamal.repository.memory.log.MemoryTopicRepository
import kotlin.reflect.KClass

object MemoryFixture : BaseTestFixture {
    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        BrokerTopicsRepository::class -> MemoryBrokerTopicsRepository() as REPO
        TopicRepository::class -> MemoryTopicRepository(Topic(TopicId(23), GroupId(1), TopicName("test-topic"))) as REPO
        else -> TODO()
    }
}