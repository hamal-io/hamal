package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.backend.repository.api.log.LogBrokerTopicsRepository
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class MemoryBrokerTopics(
    val logBrokerId: LogBroker.Id
)

class MemoryLogBrokerTopicsRepository(
    private val brokerTopics: MemoryBrokerTopics,
) : LogBrokerTopicsRepository<MemoryLogTopic> {

    private val lock = ReentrantLock()
    private val topicMapping = mutableMapOf<TopicName, MemoryLogTopic>()
    override fun resolveTopic(name: TopicName): MemoryLogTopic {
        return lock.withLock {
            topicMapping.putIfAbsent(
                name,
                MemoryLogTopic(
                    id = TopicId(topicMapping.size + 1),
                    logBrokerId = brokerTopics.logBrokerId,
                    name = name
                )
            )
            topicMapping[name]!!
        }
    }

    override fun find(topicId: TopicId): MemoryLogTopic? = lock.withLock {
        topicMapping.values.find { it.id == topicId }
    }

    override fun count(): ULong {
        return lock.withLock { topicMapping.size.toULong() }
    }

    fun clear() {
        topicMapping.clear()
    }

    override fun close() {
    }
}