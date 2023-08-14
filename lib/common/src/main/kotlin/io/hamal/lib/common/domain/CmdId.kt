package io.hamal.lib.common.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

@Serializable(with = CmdId.Serializer::class)
data class CmdId(val value: String) : Comparable<CmdId> {

    // FIXME make sure its max 255 chars

    constructor(value: ByteArray) : this(String(value))
    constructor(value: Int) : this(value.toString())
    constructor(value: Long) : this(value.toString())
    constructor(value: BigInteger) : this(value.toString())
    constructor(value: DomainId) : this(value.value.value)

    object Serializer : KSerializer<CmdId> {
        override val descriptor = PrimitiveSerialDescriptor("CmdId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = CmdId(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: CmdId) {
            encoder.encodeString(value.value)
        }
    }

    override fun compareTo(other: CmdId) = value.compareTo(other.value)

    operator fun plus(hashcode: Int): CmdId = CmdId("${value}_$hashcode")
}
