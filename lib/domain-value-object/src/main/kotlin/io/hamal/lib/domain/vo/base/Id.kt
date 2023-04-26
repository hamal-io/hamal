package io.hamal.lib.domain.vo.base

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.util.Hex
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


abstract class Id : ValueObject.ComparableImpl<Id.Value>() {
    data class Value(val value: String) : Comparable<Value> {
        init {
            IdValidator.validate(value)
        }

        override fun compareTo(other: Value) = value.compareTo(other.value)
    }

}

internal object IdValidator {
    fun validate(value: String) {
        require(Hex.isValidHexNumber(value)) { IllegalArgumentException("Id('$value') is illegal") }
    }
}


abstract class IdSerializer<ID : Id>(
    val fn: (String) -> ID
) : KSerializer<ID> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ID {
        return fn(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ID) {
        encoder.encodeString(value.value.value)
    }
}
