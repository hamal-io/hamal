package io.hamal.lib.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

//FIXME must include account id ! -
// otherwise if a second account with same cmdId performs the same task - he / she gets result of account one
@Serializable(with = CmdId.Serializer::class)
data class CmdId(val value: BigInteger) : Comparable<CmdId> {
    constructor(value: ByteArray) : this(BigInteger(value))
    constructor(value: Int) : this(value.toBigInteger())
    constructor(value: Long) : this(value.toBigInteger())
    constructor(value: String) : this(BigInteger(value, 16))

    object Serializer : KSerializer<CmdId> {
        override val descriptor = PrimitiveSerialDescriptor("CommandId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = CmdId(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: CmdId) {
            encoder.encodeString(value.value.toString(16))
        }
    }

    override fun compareTo(other: CmdId) = value.compareTo(other.value)
}
