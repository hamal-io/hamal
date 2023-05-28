package io.hamal.lib.domain.value

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

@Serializable
@SerialName("Number")
data class NumberValue(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
) : Value {

    constructor(value: Int) : this(BigDecimal.valueOf(value.toLong()))

    @Transient
    override val metaTable = DefaultNumberMetaTable

    object BigDecimalSerializer : KSerializer<BigDecimal> {
        override fun deserialize(decoder: Decoder): BigDecimal {
            return decoder.decodeString().toBigDecimal()
        }

        override fun serialize(encoder: Encoder, value: BigDecimal) {
            encoder.encodeString(value.toEngineeringString())
        }

        override val descriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
    }
}


object DefaultNumberMetaTable : MetaTable {
    override val type = "Number"
    override val operations: List<ValueOperation> = listOf()
}