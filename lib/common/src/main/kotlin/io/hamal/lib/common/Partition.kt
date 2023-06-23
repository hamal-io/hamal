package io.hamal.lib.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Partition.Serializer::class)
data class Partition(val value: Int) {
    init {
        require(value >= 0) { "Partition must not be negative - [0, 1023]" }
        require(value <= 1023) { "Partition is limited to 10 bits - [0, 1023]" }
    }

    override fun toString(): String {
        return "Partition($value)"
    }

    object Serializer : KSerializer<Partition> {
        override val descriptor = PrimitiveSerialDescriptor("Partition", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder) = Partition(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: Partition) {
            encoder.encodeInt(value.value)
        }
    }
}