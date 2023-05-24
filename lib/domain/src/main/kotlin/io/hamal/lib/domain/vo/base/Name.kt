package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.ddd.ValueObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class Name : ValueObject.ComparableImpl<Name.Value>() {
    @Serializable
    data class Value(val value: String) : Comparable<Value> {
        init {
            RefValidator.validate(value)
        }

        override fun compareTo(other: Value) = value.compareTo(other.value)
    }

    override fun toString(): String {
        return "${this::class.simpleName}(${value.value})"
    }
}

internal object RefValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        require(regex.matches(value)) { IllegalArgumentException("Reference('$value') is illegal") }
    }
}


abstract class NameSerializer<REF : Name>(
    val fn: (String) -> REF
) : KSerializer<REF> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): REF {
        return fn(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: REF) {
        encoder.encodeString(value.value.value)
    }
}
