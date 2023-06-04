package io.hamal.backend.repository.record

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.base.DomainId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface RecordType {
    val value: Int
}

@Serializable(with = RecordSequence.Serializer::class)
data class RecordSequence(val value: Int) : Comparable<RecordSequence>//FIXME becomes VO
{
    companion object {
        fun first() = RecordSequence(1)
    }

    override fun compareTo(other: RecordSequence) = other.value.compareTo(value)

    fun next() = RecordSequence(value + 1)

    object Serializer : KSerializer<RecordSequence> {
        override val descriptor = PrimitiveSerialDescriptor("RecSeq", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): RecordSequence {
            return RecordSequence(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: RecordSequence) {
            encoder.encodeInt(value.value)
        }
    }

}

interface Record<ID : DomainId> {
    val entityId: ID
    val cmdId: CmdId
    val sequence: RecordSequence
    // FIXME sval recordedAt: Instant
}

interface RecordEntity<ID : DomainId, RECORD : Record<ID>> {
    val id: ID
    val cmdId: CmdId
    val sequence: RecordSequence

    fun apply(rec: RECORD): RecordEntity<ID, RECORD>
}