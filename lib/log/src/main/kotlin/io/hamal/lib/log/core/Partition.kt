package io.hamal.lib.log.core

import java.nio.ByteBuffer
import java.nio.file.Path
import java.time.Instant

// FIXME just a pass through for now - replace with proper implementation,
// like supporting multiple segments, roll over etc

class Partition(
    private val config: Config,
    private var activeSegment: Segment
) : AutoCloseable {

    init {
        activeSegment = Segment.open(
            Segment.Config(
                id = Segment.Id(1),
                path = config.path
            )
        )
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
        data class Id(
            val partitionId: Partition.Id,
            val segmentId: Segment.Id
        )
    }

    fun countRecords() = activeSegment.countRecords()
    fun append(vararg toRecord: ToRecord): List<Record.Id> {
        return append(toRecord.toList())
    }

    fun append(toRecord: Collection<ToRecord>): List<Record.Id> {
        TODO()
        //        return activeSegment.append().map { segmentId ->
//            Record.Id(
//                partitionId = config.id,
//                segmentId = segmentId
//            )
//        }
    }

    fun read(firstId: Record.Id, limit: Int = 1): List<Record> {
        TODO()
    }

    override fun close() {
        activeSegment.close()
    }
}