package io.hamal.lib.web3.eth.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EthReqId.Serializer::class)
class EthReqId(val value: Long) {
    internal object Serializer : KSerializer<EthReqId> {
        override val descriptor = PrimitiveSerialDescriptor("EthReqId", PrimitiveKind.LONG)
        override fun deserialize(decoder: Decoder): EthReqId {
            return EthReqId(decoder.decodeLong())
        }

        override fun serialize(encoder: Encoder, value: EthReqId) {
            encoder.encodeLong(value.value)
        }
    }
}