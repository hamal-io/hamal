package io.hamal.lib.web3.eth.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EthRequestId.Serializer::class)
data class EthRequestId(val value: Int) {
    internal object Serializer : KSerializer<EthRequestId> {
        override val descriptor = PrimitiveSerialDescriptor("EthRequestId", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): EthRequestId {
            return EthRequestId(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: EthRequestId) {
            encoder.encodeInt(value.value)
        }
    }
}