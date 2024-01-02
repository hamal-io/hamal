package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class CorrelationId(
    override val value: String
) : ValueObject.ComparableImpl<String>() {

    companion object {
        val default = CorrelationId("__default__")
    }

    init {
        CorrelationIdValidator.validate(value)
    }

    object Serializer : KSerializer<CorrelationId> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("CorrelationId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): CorrelationId {
            return CorrelationId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: CorrelationId) {
            encoder.encodeString(value.value)
        }
    }

    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

internal object CorrelationIdValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        require(regex.matches(value)) { IllegalArgumentException("DomainName('$value') is illegal") }
    }
}
