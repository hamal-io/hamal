package io.hamal.lib.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

@Serializable(with = ReqId.Serializer::class)
data class ReqId(val value: BigInteger) {
    constructor(value: ByteArray) : this(BigInteger(value))
    constructor(value: Int) : this(value.toBigInteger())

    object Serializer : KSerializer<ReqId> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("RequestId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): ReqId {
            return ReqId(BigInteger(decoder.decodeString()))
        }

        override fun serialize(encoder: Encoder, value: ReqId) {
            encoder.encodeString(value.value.toString(16))
        }
    }
}
