package io.hamal.lib.common.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
abstract class IntValueObject : ValueObject.ComparableImpl<Int>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class IntValueObjectSerializer<INT : IntValueObject>(
    val fn: (Int) -> INT
) : KSerializer<INT> {
    override val descriptor = PrimitiveSerialDescriptor("IntVO", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): INT {
        return fn(decoder.decodeInt())
    }

    override fun serialize(encoder: Encoder, value: INT) {
        encoder.encodeInt(value.value)
    }
}

