package io.hamal.lib.domain.vo.base

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.throwIf
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
    private val regex = Regex("^([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})$")
    fun validate(value: String) {
        throwIf(!regex.matches(value)) { IllegalArgumentException("Id('$value') is illegal") }
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
