package io.hamal.lib.web3.eth.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EthReqId.Serializer::class)
data class EthReqId(val value: Int) {
    internal object Serializer : KSerializer<EthReqId> {
        override val descriptor = PrimitiveSerialDescriptor("EthReqId", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): EthReqId {
            return EthReqId(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: EthReqId) {
            encoder.encodeInt(value.value)
        }
    }
}