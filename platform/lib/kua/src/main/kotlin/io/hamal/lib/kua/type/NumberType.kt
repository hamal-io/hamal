package io.hamal.lib.kua.type

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.DecimalFormat

@Serializable
@SerialName("NumberType")
data class NumberType(
    @Serializable(with = Serializer::class)
    val value: Double
) : SerializableType() {
    constructor(value: Int) : this(value.toDouble())

    companion object {
        val Zero = NumberType(0.0)
        val One = NumberType(1.0)
    }

    operator fun times(value: Int) = NumberType(this.value * value)
    operator fun times(value: Double) = NumberType(this.value * value)

    object Serializer : KSerializer<Double> {
        override val descriptor = PrimitiveSerialDescriptor("DT", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Double {
            return decoder.decodeString().toDouble()
        }

        override fun serialize(encoder: Encoder, value: Double) {
            encoder.encodeString(formatter.format(value))
        }

        private val formatter = DecimalFormat().apply {
            isGroupingUsed = false
            minimumFractionDigits = 0
            maximumFractionDigits = 6
            isDecimalSeparatorAlwaysShown = false
        }
    }

    override fun toString(): String {
        return "NumberType(${value.toBigDecimal().toPlainString()})"
    }
}