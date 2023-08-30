package io.hamal.repository.memory.log

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.log.BrokerConsumersRepository
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.GroupId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MemoryBrokerConsumersRepository : BrokerConsumersRepository {

    internal val lock = ReentrantLock()
    internal val store = mutableMapOf<Pair<GroupId, TopicId>, ChunkId>()
    override fun nextChunkId(groupId: GroupId, topicId: TopicId): ChunkId {
        return lock.withLock {
            store[Pair(groupId, topicId)] ?: ChunkId(0)
        }
    }

    override fun commit(groupId: GroupId, topicId: TopicId, chunkId: ChunkId) {
        return lock.withLock {
            store.putIfAbsent(Pair(groupId, topicId), ChunkId(0))
            store[Pair(groupId, topicId)] = ChunkId(SnowflakeId(chunkId.value.value + 1))
            store[Pair(groupId, topicId)]
        }
    }

    override fun close() {}
    override fun count(): ULong = lock.withLock { store.size.toULong() }
}

fun MemoryBrokerConsumersRepository.clear() {
    lock.withLock { store.clear() }
}