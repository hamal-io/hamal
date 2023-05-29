package io.hamal.lib.common

import io.hamal.lib.common.SnowflakeId.ElapsedSource.Elapsed
import io.hamal.lib.common.SnowflakeId.SequenceSource
import io.hamal.lib.common.SnowflakeId.SequenceSource.*
import io.hamal.lib.common.util.BitUtils
import io.hamal.lib.domain.Tuple2
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

    interface ElapsedSource {
        fun elapsed(): Elapsed

        @JvmInline
        value class Elapsed(val value: Long) : Comparable<Elapsed> {
            override fun compareTo(other: Elapsed) = value.compareTo(other.value)
        }
    }

    interface ShardSource {
        fun get(): Shard
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
        fun next(): SnowflakeId
    }

    override fun compareTo(other: SnowflakeId) = value.compareTo(other.value)

    fun shard(): Shard = Shard(
        BitUtils.extractRange(
            value = value,
            startIndex = 53,
            numberOfBits = 10
        ).toInt()
    )

    fun sequence(): Sequence = Sequence(
        BitUtils.extractRange(
            value = value,
            startIndex = 41,
            numberOfBits = 12
        ).toShort()
    )


    fun elapsed(): Elapsed = Elapsed(
        BitUtils.extractRange(
            value = value,
            startIndex = 0,
            numberOfBits = 41
        )
    )

    override fun toString(): String {
        return "$value"
    }

    object Serializer : KSerializer<SnowflakeId> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("SnowflakeId", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder) = SnowflakeId(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: SnowflakeId) {
            encoder.encodeLong(value.value)
        }
    }
}


class DefaultElapsedSource(
    val epoch: Long = 1682116276624 // 2023-04-22 some when in the morning AEST
) : SnowflakeId.ElapsedSource {
    override fun elapsed() = Elapsed(millis())

    private fun millis() = System.currentTimeMillis() - epoch
}

class DefaultShardSource(
    private val shard: Shard
) : SnowflakeId.ShardSource {
    constructor(value: Int) : this(Shard(value))

    override fun get() = shard
}

class DefaultSequenceSource : SequenceSource {

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

class FixedElapsedSource(val value: Long) : SnowflakeId.ElapsedSource {
    override fun elapsed() = Elapsed(value)
}

class SnowflakeGenerator(
    private val shardSource: SnowflakeId.ShardSource,
    private val elapsedSource: SnowflakeId.ElapsedSource = DefaultElapsedSource(),
    private val sequenceSource: SequenceSource = DefaultSequenceSource(),
    private val lock: Lock = ReentrantLock()
) : SnowflakeId.Generator {

    override fun next(): SnowflakeId {
        return lock.withLock {
            val shard = shardSource.get()
            val (elapsed, sequence) = sequenceSource.next(elapsedSource::elapsed)
            generate(elapsed, shard, sequence)
        }
    }

    private fun generate(
        elapsed: Elapsed,
        shard: Shard,
        sequence: Sequence
    ): SnowflakeId {
        val shardValue = shard.value.toLong().shl(53)
        val sequenceValue = sequence.value.toLong().shl(41)
        val elapsedValue = elapsed.value
        val result = shardValue + sequenceValue + elapsedValue
        return SnowflakeId(result)
    }
}