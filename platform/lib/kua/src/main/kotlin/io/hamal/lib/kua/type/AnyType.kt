package io.hamal.lib.kua.type

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class AnyType(val value: Type) : Type

@Serializable(with = AnySerializableType.Serializer::class)
data class AnySerializableType(val value: SerializableType) : SerializableType() {
    object Serializer : KSerializer<AnySerializableType> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

        override fun deserialize(decoder: Decoder): AnySerializableType {
            return AnySerializableType(decoder.decodeSerializableValue(SerializableType.serializer()))
        }

        override fun serialize(encoder: Encoder, value: AnySerializableType) {
            encoder.encodeSerializableValue(SerializableType.serializer(), value.value)
        }
    }
}