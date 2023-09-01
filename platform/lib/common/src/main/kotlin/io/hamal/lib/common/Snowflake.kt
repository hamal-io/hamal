package io.hamal.lib.common

import io.hamal.lib.common.SnowflakeId.ElapsedSource.Elapsed
import io.hamal.lib.common.SnowflakeId.SequenceSource
import io.hamal.lib.common.SnowflakeId.SequenceSource.*
import io.hamal.lib.common.util.BitUtils
import io.hamal.lib.common.Tuple2
import io.hamal.lib.common.domain.DomainId
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

    interface ElapsedSource {
        fun elapsed(): Elapsed

        @JvmInline
        value class Elapsed(val value: Long) : Comparable<Elapsed> {
            override fun compareTo(other: Elapsed) = value.compareTo(other.value)
        }
    }

    interface PartitionSource {
        fun get(): Partition
    }

    interface SequenceSource {
        /**
         * Blocks if sequence is exhausted until next milliseconds
         */
        fun next(elapsedSource: () -> Elapsed): Pair<Elapsed, Sequence>

        @JvmInline
        value class Sequence(val value: Short) {
            constructor(value: Int) : this(value.toShort())

            init {
                require(value >= 0) { "Sequence must not be negative - [0, 4095]" }
                require(value <= 4095) { "Sequence is limited to 12 bits - [0, 4095]" }
            }
        }
    }

    interface Generator {
        fun next(): SnowflakeId
    }

    override fun compareTo(other: SnowflakeId) = value.compareTo(other.value)

    fun partition(): Partition = Partition(
        BitUtils.extractRange(
            value = value,
            startIndex = 0,
            numberOfBits = 10
        ).toInt()
    )

    fun sequence(): Sequence = Sequence(
        BitUtils.extractRange(
            value = value,
            startIndex = 10,
            numberOfBits = 12
        ).toShort()
    )


    fun elapsed(): Elapsed = Elapsed(
        BitUtils.extractRange(
            value = value,
            startIndex = 22,
            numberOfBits = 41
        )
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


class DefaultElapsedSource(
    val epoch: Long = 1682116276624 // 2023-04-22 some when in the morning AEST
) : SnowflakeId.ElapsedSource {
    override fun elapsed() = Elapsed(millis())

    private fun millis() = System.currentTimeMillis() - epoch
}

class DefaultPartitionSource(
    private val partition: Partition
) : SnowflakeId.PartitionSource {
    constructor(value: Int) : this(Partition(value))

    override fun get() = partition
}

class DefaultSequenceSource : SequenceSource {

    private var previousCalledAt: Elapsed = Elapsed(0)
    private var nextSequence = 0
    private val maxSequence = 4096

    override fun next(elapsedSource: () -> Elapsed): Pair<Elapsed, Sequence> {
        val elapsed = elapsedSource()
        check(elapsed >= previousCalledAt) { "Elapsed must be monotonic" }

        if (elapsed == previousCalledAt) {
            if (nextSequence >= maxSequence) {
                while (true) {
                    val currentElapsed = elapsedSource()
                    if (elapsed != currentElapsed) {
                        nextSequence = 0
                        previousCalledAt = currentElapsed
                        break
                    }
                }
            } else {
                nextSequence
                previousCalledAt = elapsed
            }

        } else {
            previousCalledAt = elapsed
            nextSequence = 0
        }

        return Pair(previousCalledAt, Sequence(nextSequence++))
    }
}

class FixedElapsedSource(val value: Long) : SnowflakeId.ElapsedSource {
    override fun elapsed() = Elapsed(value)
}

class SnowflakeGenerator(
    private val partitionSource: SnowflakeId.PartitionSource,
    private val elapsedSource: SnowflakeId.ElapsedSource = DefaultElapsedSource(),
    private val sequenceSource: SequenceSource = DefaultSequenceSource(),
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
        val sequenceValue = sequence.value.toLong().shl(10)
        val partitionValue = partition.value.toLong()
        val result = elapsedValue + sequenceValue + partitionValue
        return SnowflakeId(result)
    }
}