package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.domain.Shard
import java.nio.file.Path


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, sharding by key
// keeping track of consumer group ids
class DefaultTopicRepository(
    internal val topic: Topic
) : BaseRepository(
    object : Config {
        override val path: Path get() = topic.path
        override val filename: String get() = String.format("topic-%05d", topic.id.value.toLong())
        override val shard: Shard get() = topic.shard

    }
), TopicRepository {

    internal var activePartition: Partition
    internal var activePartitionRepository: PartitionRepository

    init {
        activePartition = Partition(
            id = Partition.Id(1),
            topicId = topic.id,
            path = topic.path.resolve(config.filename),
            shard = topic.shard
        )
        activePartitionRepository = DefaultPartitionRepository(activePartition)
    }

    override fun setupConnection(connection: Connection) {}

    override fun setupSchema(connection: Connection) {}

    override fun append(bytes: ByteArray): Chunk.Id {
        return activePartitionRepository.append(bytes)
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        return activePartitionRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activePartitionRepository.count()
    }

    override fun clear() {
        activePartitionRepository.clear()
    }

    override fun close() {
        activePartitionRepository.close()
    }

}
