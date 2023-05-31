package io.hamal.lib.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

@Serializable(with = ReqId.Serializer::class)
data class ReqId(val value: BigInteger) : Comparable<ReqId> {
    constructor(value: ByteArray) : this(BigInteger(value))
    constructor(value: Int) : this(value.toBigInteger())
    constructor(value: Long) : this(value.toBigInteger())
    constructor(value: String) : this(BigInteger(value, 16))

    object Serializer : KSerializer<ReqId> {
        override val descriptor = PrimitiveSerialDescriptor("ReqId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = ReqId(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: ReqId) {
            encoder.encodeString(value.value.toString(16))
        }
    }

    override fun compareTo(other: ReqId) = value.compareTo(other.value)
}
