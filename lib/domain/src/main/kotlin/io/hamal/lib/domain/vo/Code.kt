package io.hamal.lib.domain.vo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = Code.Serializer::class)
data class Code(
    val value: String
) {
    internal object Serializer : KSerializer<Code> {
        override val descriptor = PrimitiveSerialDescriptor("Code", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = Code(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: Code) {
            encoder.encodeString(value.value)
        }
    }
}