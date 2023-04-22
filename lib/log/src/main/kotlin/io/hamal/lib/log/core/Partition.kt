package io.hamal.lib.log.core

import java.lang.String
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.Path

// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple segments, roll over etc


// fixme the segment.record.id is supposed to be relative to partition.record.id index, as there is only one segment per partition in the mean time its just a passthrough
class Partition(
    private val path: Path,
    private val id: Id,
    internal var activeSegment: Segment
) : AutoCloseable {

    companion object {
        fun open(config: Config): Partition {
            val path = ensureDirectoryExists(config)
            return Partition(
                path = path,
                id = config.id,
                activeSegment = Segment.open(
                    Segment.Config(
                        id = Segment.Id(0),
                        path = path
                    )
                )
            )
        }

        private fun ensureDirectoryExists(config: Config): Path {
            val result = config.path.resolve(Path(String.format("%04d", config.id.value)))
            Files.createDirectories(result)
            return result
        }
    }

    init {

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
        val key: ByteBuffer,
        val value: ByteBuffer,
        val instant: Instant
    ) {
        @JvmInline
        value class Id(val value: Long) {
            constructor(value: Int) : this(value.toLong())
        }
    }

    fun countRecords() = activeSegment.countRecords()
    fun append(vararg toRecord: ToRecord): List<Record.Id> {
        return append(toRecord.toList())
    }

    fun append(toRecord: Collection<ToRecord>): List<Record.Id> {
        return activeSegment.append(toRecord).map { segmentId ->
            //fixme the segment.record.id is relative to partition.record.id index
            Record.Id(segmentId.value)
        }
    }

    fun read(firstId: Record.Id, limit: Int = 1): List<Record> {
        return activeSegment.read(
            Segment.Id(firstId.value),
            limit
        ).map { segmentRecord ->
            //fixme the segment.record.id is relative to partition.record.id index
            Record(
                id = Record.Id(segmentRecord.id.value),
                segmentId = activeSegment.baseId,
                partitionId = id,
                key = segmentRecord.key,
                value = segmentRecord.value,
                instant = segmentRecord.instant
            )
        }
    }

    override fun close() {
        activeSegment.close()
    }
}

internal fun Partition.clear() {
    activeSegment.clear()
}
