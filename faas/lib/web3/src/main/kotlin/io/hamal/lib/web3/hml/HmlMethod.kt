package io.hamal.lib.web3.hml

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = HmlMethod.Serializer::class)
enum class HmlMethod(val value: String) {
    Call("eth_call"),
    GetBlockByNumber("eth_get_block_by_number");

    object Serializer : KSerializer<HmlMethod> {
        override val descriptor = PrimitiveSerialDescriptor("HmlMethod", STRING)

        override fun deserialize(decoder: Decoder): HmlMethod {
            val method = decoder.decodeString()
            return HmlMethod.values().find { it.value == method }
                ?: throw NoSuchElementException("HmlMethod not found")
        }

        override fun serialize(encoder: Encoder, value: HmlMethod) {
            encoder.encodeString(value.value)
        }

    }
}