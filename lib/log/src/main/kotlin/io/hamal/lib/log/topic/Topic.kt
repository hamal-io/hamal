package io.hamal.lib.log.topic

import io.hamal.lib.log.broker.Broker
import io.hamal.lib.log.partition.Partition
import io.hamal.lib.log.partition.PartitionRepository
import io.hamal.lib.log.partition.clear
import io.hamal.lib.log.segment.Chunk
import io.hamal.lib.log.segment.ChunkAppender
import io.hamal.lib.log.segment.ChunkCounter
import io.hamal.lib.log.segment.ChunkReader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

data class Topic(
    val id: Id,
    val brokerId: Broker.Id,
    val name: Name,
    val path: Path
) {

    @JvmInline
    value class Name(val value: String)

    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}


// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, sharding by key
// keeping track of consumer group ids
class TopicRepository private constructor(
    internal val activePartition: Partition,
    internal val activePartitionRepository: PartitionRepository
) : ChunkAppender, ChunkReader, ChunkCounter, AutoCloseable {

    companion object {
        fun open(topic: Topic): TopicRepository {
            val path = ensureDirectoryExists(topic)
            val partition = Partition(
                id = Partition.Id(1),
                topicId = topic.id,
                path = path
            )
            return TopicRepository(
                activePartition = partition,
                activePartitionRepository = PartitionRepository.open(partition)
            )
        }

        private fun ensureDirectoryExists(topic: Topic): Path {
            val result = topic.path.resolve(Path(String.format("topic-%05d", topic.id.value.toLong())))
            Files.createDirectories(result)
            return result
        }
    }

    override fun close() {
        activePartitionRepository.close()
    }

    override fun append(vararg bytes: ByteArray): List<Chunk.Id> {
        return activePartitionRepository.append(*bytes)
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        return activePartitionRepository.read(firstId, limit)
    }

    override fun count(): ULong {
        return activePartitionRepository.count()
    }
}

internal fun TopicRepository.clear() {
    activePartitionRepository.clear()
}