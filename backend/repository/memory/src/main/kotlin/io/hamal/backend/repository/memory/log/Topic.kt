package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName

data class MemoryLogTopic(
    override val id: TopicId,
    override val logBrokerId: LogBroker.Id,
    override val name: TopicName,
    override val shard: Shard
) : LogTopic


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, sharding by key
// keeping track of consumer group ids
class MemoryLogTopicRepository(
    internal val topic: MemoryLogTopic
) : LogTopicRepository {

    internal var activeLogShard: MemoryLogShard
    internal var activeLogShardRepository: MemoryLogShardRepository

    init {
        activeLogShard = MemoryLogShard(
            id = topic.shard,
            topicId = topic.id
        )
        activeLogShardRepository = MemoryLogShardRepository(activeLogShard)
    }

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        activeLogShardRepository.append(cmdId, bytes)
    }

    override fun read(firstId: LogChunkId, limit: Int): List<LogChunk> {
        return activeLogShardRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activeLogShardRepository.count()
    }

    override fun clear() {
        activeLogShardRepository.clear()
    }

    override fun close() {
        activeLogShardRepository.close()
    }
}
