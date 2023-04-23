package io.hamal.lib.log.topic

import io.hamal.lib.log.ToRecord
import io.hamal.lib.log.partition.Partition
import io.hamal.lib.log.partition.clear
import io.hamal.lib.log.segment.Segment
import java.lang.String
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.Path

// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple partitions, sharding by key
// keeping track of consumer group ids

class Topic(
    private val id: Id,
    internal val partitions: List<Partition>
) : AutoCloseable {

    companion object {
        fun open(config: Config): Topic {
            val path = ensureDirectoryExists(config)
            return Topic(
                id = config.id,
                partitions = listOf(
                    Partition.open(
                        Partition.Config(
                            id = Partition.Id(1),
                            path = path
                        )
                    )
                )
            )
        }

        private fun ensureDirectoryExists(config: Config): Path {
            val result = config.path.resolve(Path(String.format("topic-%05d", config.id.value)))
            Files.createDirectories(result)
            return result
        }

    }

    @JvmInline
    value class Id(val value: Long) {
        constructor(value: Int) : this(value.toLong())
    }

    data class Config(
        val id: Id,
        val path: Path
    )

    data class Record(
        val id: Id,
        val segmentId: Segment.Id,
        val partitionId: Partition.Id,
        val topicId: Topic.Id,
        val key: ByteBuffer,
        val value: ByteBuffer,
        val instant: Instant
    ) {
        @JvmInline
        value class Id(val value: Long) {
            constructor(value: Int) : this(value.toLong())
        }
    }

    fun countRecords(): Long = partitions.sumOf { it.countRecords() }

    fun append(vararg toRecord: ToRecord): List<Record.Id> {
        return append(toRecord.toList())
    }

    fun append(toRecord: Collection<ToRecord>): List<Record.Id> {
        return partitions.first().append(toRecord).map { partitionId ->
            //fixme the partition.record.id is relative to topic.record.id index
            Record.Id(partitionId.value)
        }
    }

    fun read(firstId: Record.Id, limit: Int = 1): List<Record> {
        return partitions.first().read(
            Partition.Record.Id(firstId.value),
            limit
        ).map { partitionRecord ->
            //fixme the segment.record.id is relative to partition.record.id index
            Record(
                id = Record.Id(partitionRecord.id.value),
                segmentId = partitionRecord.segmentId,
                partitionId = partitionRecord.partitionId,
                topicId = id,
                key = partitionRecord.key,
                value = partitionRecord.value,
                instant = partitionRecord.instant
            )
        }
    }

    override fun close() {
        partitions.forEach { partition ->
            partition.close()
        }
    }
}

internal fun Topic.clear() {
    partitions.forEach { it.clear() }
}