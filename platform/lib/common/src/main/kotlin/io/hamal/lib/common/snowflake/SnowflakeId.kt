package io.hamal.lib.common.snowflake

import io.hamal.lib.common.Partition
import io.hamal.lib.common.util.BitUtils


@JvmInline
value class SnowflakeId(val value: Long) : Comparable<SnowflakeId> {
    constructor(value: Int) : this(value.toLong())
    constructor(value: String) : this(value.toLong(16))

    interface Generator {
        fun next(): SnowflakeId
    }

    override fun compareTo(other: SnowflakeId) = value.compareTo(other.value)

    fun sequence(): Sequence = Sequence(
        BitUtils.extractRange(value = value, startIndex = 0, numberOfBits = 16).toInt()
    )

    fun partition(): Partition =
        Partition(BitUtils.extractRange(value = value, startIndex = 16, numberOfBits = 6).toInt())

    fun elapsed(): Elapsed = Elapsed(
        BitUtils.extractRange(value = value, startIndex = 22, numberOfBits = 41)
    )

    override fun toString(): String {
        return value.toString()
    }

    fun toInt(): Int = value.toInt()

    fun toLong(): Long = value.toLong()
}


class SnowflakeGenerator(
    private val partitionSource: PartitionSource,
    private val elapsedSource: ElapsedSource = ElapsedSourceImpl(),
    private val sequenceSource: SequenceSource = SequenceSourceImpl()
) : SnowflakeId.Generator {

    override fun next(): SnowflakeId {
        val partition = partitionSource.get()
        val (elapsed, sequence) = sequenceSource.next(elapsedSource::elapsed)
        return generate(elapsed, partition, sequence)
    }

    private fun generate(
        elapsed: Elapsed,
        partition: Partition,
        sequence: Sequence
    ): SnowflakeId {
        val elapsedValue = elapsed.value.shl(22)
        val partitionValue = partition.value.toLong().shl(16)
        val sequenceValue = sequence.value.toLong()
        val result = elapsedValue + partitionValue + sequenceValue
        return SnowflakeId(result)
    }
}