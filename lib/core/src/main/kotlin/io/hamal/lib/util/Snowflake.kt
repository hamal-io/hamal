package io.hamal.lib.util

import io.hamal.lib.Tuple2
import io.hamal.lib.util.Snowflake.ElapsedSource.Elapsed
import io.hamal.lib.util.Snowflake.PartitionSource.Partition
import io.hamal.lib.util.Snowflake.SequenceSource.Sequence
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


interface Snowflake {

    interface ElapsedSource {
        fun elapsed(): Elapsed

        @JvmInline
        value class Elapsed(val value: Long) : Comparable<Elapsed> {
            override fun compareTo(other: Elapsed) = value.compareTo(other.value)
        }
    }

    interface PartitionSource {
        fun get(): Partition

        @JvmInline
        value class Partition(val value: Short) {
            constructor(value: Int) : this(value.toShort())

            init {
                require(value >= 0) { "Partition must not be negative - [0, 1023]" }
                require(value <= 1023) { "Partition is limited to 10 bits - [0, 1023]" }
            }
        }
    }

    interface SequenceSource {
        /**
         * Blocks if sequence is exhausted until next milliseconds
         */
        fun next(elapsedSource: () -> Elapsed): Tuple2<Elapsed, Sequence>

        @JvmInline
        value class Sequence(val value: Short) {
            constructor(value: Int) : this(value.toShort())

            init {
                require(value > 0) { "Sequence must be positive - [1, 4096]" }
                require(value <= 4096) { "Sequence is limited to 12 bits - [1, 4096]" }
            }
        }
    }

    interface Generator {
        fun next(): Id
    }

    @JvmInline
    @Serializable(with = Id.Serializer::class)
    value class Id(val value: Long) : Comparable<Id> {

        override fun compareTo(other: Id) = value.compareTo(other.value)

        fun partition(): Partition = Partition(
            Bitwise.extractRange(
                value = value,
                startIndex = 53,
                numberOfBits = 10
            ).toShort()
        )

        fun sequence(): Sequence = Sequence(
            Bitwise.extractRange(
                value = value,
                startIndex = 41,
                numberOfBits = 12
            ).toShort()
        )


        fun elapsed(): Elapsed = Elapsed(
            Bitwise.extractRange(
                value = value,
                startIndex = 0,
                numberOfBits = 41
            )
        )

        override fun toString(): String {
            return "$value"
        }

        object Serializer : KSerializer<Id> {
            override val descriptor: SerialDescriptor
                get() = PrimitiveSerialDescriptor("SnowflakeId", PrimitiveKind.LONG)

            override fun deserialize(decoder: Decoder) = Id(decoder.decodeLong())

            override fun serialize(encoder: Encoder, value: Id) {
                encoder.encodeLong(value.value)
            }
        }
    }
}

class DefaultElapsedSource(
    val epoch: Long = 1682116276624 // 2023-04-22 some when in the morning AEST
) : Snowflake.ElapsedSource {
    override fun elapsed() = Elapsed(millis())

    private fun millis() = System.currentTimeMillis() - epoch
}

class DefaultPartitionSource(
    private val partition: Partition
) : Snowflake.PartitionSource {
    constructor(value: Int) : this(Partition(value))

    override fun get() = partition
}

class DefaultSequenceSource : Snowflake.SequenceSource {

    private var previousCalledAt: Elapsed = Elapsed(0)
    private var nextSequence = 0
    private val maxSequence = 4096

    override fun next(elapsedSource: () -> Elapsed): Tuple2<Elapsed, Sequence> {
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

        return Tuple2(previousCalledAt, Sequence(++nextSequence))
    }
}

internal class FixedElapsedSource(val value: Long) : Snowflake.ElapsedSource {
    override fun elapsed() = Elapsed(value)
}

class SnowflakeGenerator(
    private val partitionSource: Snowflake.PartitionSource,
    private val elapsedSource: Snowflake.ElapsedSource = DefaultElapsedSource(),
    private val sequenceSource: Snowflake.SequenceSource = DefaultSequenceSource(),
    private val lock: Lock = ReentrantLock()
) : Snowflake.Generator {

    override fun next(): Snowflake.Id {
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
    ): Snowflake.Id {
        val partitionValue = partition.value.toLong().shl(53)
        val sequenceValue = sequence.value.toLong().shl(41)
        val elapsedValue = elapsed.value
        val result = partitionValue + sequenceValue + elapsedValue
        return Snowflake.Id(result)
    }
}