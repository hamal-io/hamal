package io.hamal.lib.vo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Shard.Serializer::class)
data class Shard(val value: Int) {
    init {
        require(value in 0..1023) { "Shard must be in interval [0,1023]" }
    }

    override fun toString(): String {
        return "Shard($value)"
    }

    object Serializer : KSerializer<Shard> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Shard", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): Shard {
            return Shard(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: Shard) {
            encoder.encodeInt(value.value)
        }
    }
}