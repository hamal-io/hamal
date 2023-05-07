package io.hamal.lib.vo.base

import io.hamal.lib.ddd.base.ValueObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

fun <ID : DomainId, REFERENCE : Reference> referenceFromId(id: ID, ctor: (String) -> REFERENCE): REFERENCE {
    return ctor("${id.value.toString()}-ref")
}

abstract class Reference : ValueObject.ComparableImpl<Reference.Value>() {
    @Serializable
    data class Value(val value: String) : Comparable<Value> {
        init {
            ReferenceValidator.validate(value)
        }

        override fun compareTo(other: Value) = value.compareTo(other.value)
    }

    override fun toString(): String {
        return "${this::class.simpleName}(${value.value})"
    }
}

internal object ReferenceValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        require(regex.matches(value)) { IllegalArgumentException("Reference('$value') is illegal") }
    }
}


abstract class ReferenceSerializer<REFERENCE : Reference>(
    val fn: (String) -> REFERENCE
) : KSerializer<REFERENCE> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): REFERENCE {
        return fn(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: REFERENCE) {
        encoder.encodeString(value.value.value)
    }
}
