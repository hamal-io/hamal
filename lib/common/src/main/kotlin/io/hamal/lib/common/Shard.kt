package io.hamal.lib.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Shard.Serializer::class)
data class Shard(val value: Int) {
    init {
        require(value >= 0) { "Shard must not be negative - [0, 1023]" }
        require(value <= 1023) { "Shard is limited to 10 bits - [0, 1023]" }
    }

    override fun toString(): String {
        return "Shard($value)"
    }

    object Serializer : KSerializer<Shard> {
        override val descriptor = PrimitiveSerialDescriptor("Shard", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder) = Shard(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: Shard) {
            encoder.encodeInt(value.value)
        }
    }
}