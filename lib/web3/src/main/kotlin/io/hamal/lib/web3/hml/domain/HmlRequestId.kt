package io.hamal.lib.web3.hml.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = HmlRequestId.Serializer::class)
data class HmlRequestId(val value: Int) {
    internal object Serializer : KSerializer<HmlRequestId> {
        override val descriptor = PrimitiveSerialDescriptor("HamalRequestId", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): HmlRequestId {
            return HmlRequestId(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: HmlRequestId) {
            encoder.encodeInt(value.value)
        }
    }
}