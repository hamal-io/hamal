package io.hamal.repository.memory.log

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.BrokerConsumersRepository
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.ConsumerId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class BrokerConsumersMemoryRepository : BrokerConsumersRepository {

    internal val lock = ReentrantLock()
    internal val store = mutableMapOf<Pair<ConsumerId, TopicId>, ChunkId>()
    override fun nextChunkId(consumerId: ConsumerId, topicId: TopicId): ChunkId {
        return lock.withLock {
            store[Pair(consumerId, topicId)] ?: ChunkId(0)
        }
    }

    override fun commit(consumerId: ConsumerId, topicId: TopicId, chunkId: ChunkId) {
        return lock.withLock {
            store.putIfAbsent(Pair(consumerId, topicId), ChunkId(0))
            store[Pair(consumerId, topicId)] = ChunkId(chunkId.value.toInt() + 1)
            store[Pair(consumerId, topicId)]
        }
    }

    override fun close() {}
    override fun count(): ULong = lock.withLock { store.size.toULong() }
    override fun clear() {
        lock.withLock {
            store.clear()
        }
    }
}

fun BrokerConsumersMemoryRepository.clear() {
    lock.withLock { store.clear() }
}