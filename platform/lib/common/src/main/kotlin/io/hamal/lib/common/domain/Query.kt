package io.hamal.lib.common.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Limit.Serializer::class)
data class Limit(
    override val value: Int
) : ValueObject.ComparableImpl<Int>() {

    init {
        require(value > 0) { "Limit must be positive" }
    }

    object Serializer : KSerializer<Limit> {
        override val descriptor = PrimitiveSerialDescriptor("Limit", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Limit {
            return Limit(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: Limit) {
            encoder.encodeInt(value.value)
        }
    }

    companion object {
        val all = Limit(Int.MAX_VALUE)
    }
}