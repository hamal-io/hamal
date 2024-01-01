package io.hamal.lib.common.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
abstract class ValueObjectString : ValueObject.ComparableImpl<String>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class StringValueObjectSerializer<STRING : ValueObjectString>(
    val fn: (String) -> STRING
) : KSerializer<STRING> {
    override val descriptor = PrimitiveSerialDescriptor("StringVO", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): STRING {
        return fn(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: STRING) {
        encoder.encodeString(value.value)
    }
}

