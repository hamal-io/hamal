package io.hamal.lib.vo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RegionId.Serializer::class)
data class RegionId(val value: Int) {
    init {
        require(value in 0..1023) { "RegionId must be in interval [0,1023]" }
    }

    object Serializer : KSerializer<RegionId> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("RegionId", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): RegionId {
            return RegionId(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: RegionId) {
            encoder.encodeInt(value.value)
        }
    }
}