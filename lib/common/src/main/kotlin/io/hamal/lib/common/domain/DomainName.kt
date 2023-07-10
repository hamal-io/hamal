package io.hamal.lib.common.domain

import io.hamal.lib.common.domain.ValueObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
abstract class DomainName : ValueObject.ComparableImpl<String>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

internal object DomainNameValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        require(regex.matches(value)) { IllegalArgumentException("DomainName('$value') is illegal") }
    }
}


abstract class DomainNameSerializer<NAME : DomainName>(
    val fn: (String) -> NAME
) : KSerializer<NAME> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): NAME {
        return fn(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: NAME) {
        encoder.encodeString(value.value)
    }
}

