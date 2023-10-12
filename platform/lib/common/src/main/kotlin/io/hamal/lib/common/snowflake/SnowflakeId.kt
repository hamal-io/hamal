package io.hamal.lib.common.snowflake

import io.hamal.lib.common.Partition
import io.hamal.lib.common.util.BitUtils
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


@JvmInline
@Serializable(with = SnowflakeId.Serializer::class)
value class SnowflakeId(val value: Long) : Comparable<SnowflakeId> {
    constructor(value: String) : this(value.toLong(16))

    interface Generator {
        fun next(): SnowflakeId
    }

    override fun compareTo(other: SnowflakeId) = value.compareTo(other.value)

    fun sequence(): Sequence = Sequence(
        BitUtils.extractRange(value = value, startIndex = 0, numberOfBits = 12).toShort()
    )

    fun partition(): Partition =
        Partition(BitUtils.extractRange(value = value, startIndex = 12, numberOfBits = 10).toInt())

    fun elapsed(): Elapsed = Elapsed(
        BitUtils.extractRange(value = value, startIndex = 22, numberOfBits = 41)
    )

    override fun toString(): String {
        return "$value"
    }

    object Serializer : KSerializer<SnowflakeId> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("SnowflakeId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) = SnowflakeId(decoder.decodeString().toLong(16))

        override fun serialize(encoder: Encoder, value: SnowflakeId) {
            encoder.encodeString(value.value.toString(16))
        }
    }
}


class SnowflakeGenerator(
    private val partitionSource: PartitionSource,
    private val elapsedSource: ElapsedSource = ElapsedSourceImpl(),
    private val sequenceSource: SequenceSource = SequenceSourceImpl(),
    private val lock: Lock = ReentrantLock()
) : SnowflakeId.Generator {

    override fun next(): SnowflakeId {
        return lock.withLock {
            val partition = partitionSource.get()
            val (elapsed, sequence) = sequenceSource.next(elapsedSource::elapsed)
            generate(elapsed, partition, sequence)
        }
    }

    private fun generate(
        elapsed: Elapsed,
        partition: Partition,
        sequence: Sequence
    ): SnowflakeId {
        val elapsedValue = elapsed.value.shl(22)
        val partitionValue = partition.value.toLong().shl(12)
        val sequenceValue = sequence.value.toLong()
        val result = elapsedValue + partitionValue + sequenceValue
        return SnowflakeId(result)
    }
}