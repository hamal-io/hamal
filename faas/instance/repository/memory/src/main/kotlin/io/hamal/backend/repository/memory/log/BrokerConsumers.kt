package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerConsumersRepository
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.TopicId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MemoryLogBrokerConsumersRepository : LogBrokerConsumersRepository {

    internal val lock = ReentrantLock()
    internal val store = mutableMapOf<Pair<GroupId, TopicId>, LogChunkId>()
    override fun nextChunkId(groupId: GroupId, topicId: TopicId): LogChunkId {
        return lock.withLock {
            store[Pair(groupId, topicId)] ?: LogChunkId(0)
        }
    }

    override fun commit(groupId: GroupId, topicId: TopicId, chunkId: LogChunkId) {
        return lock.withLock {
            store.putIfAbsent(Pair(groupId, topicId), LogChunkId(0))
            store[Pair(groupId, topicId)] = LogChunkId(SnowflakeId(chunkId.value.value + 1))
            store[Pair(groupId, topicId)]
        }
    }

    override fun close() {}
    override fun count(): ULong = lock.withLock { store.size.toULong() }
}

fun MemoryLogBrokerConsumersRepository.clear() {
    lock.withLock { store.clear() }
}