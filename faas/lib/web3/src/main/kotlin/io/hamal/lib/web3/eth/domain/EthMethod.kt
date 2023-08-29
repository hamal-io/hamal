package io.hamal.lib.web3.eth.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EthMethod.Serializer::class)
enum class EthMethod(val value: String) {
    Call("eth_call"),
    GetBlockByNumber("eth_getBlockByNumber");

    object Serializer : KSerializer<EthMethod> {
        override val descriptor = PrimitiveSerialDescriptor("EthMethod", STRING)

        override fun deserialize(decoder: Decoder): EthMethod {
            val method = decoder.decodeString()
            return EthMethod.values().find { it.value == method }
                ?: throw NoSuchElementException("EthMethod not found")
        }

        override fun serialize(encoder: Encoder, value: EthMethod) {
            encoder.encodeString(value.value)
        }

    }
}