package io.hamal.repository.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.domain.vo.RecordedAt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

abstract class Record<ID : DomainId> {
    abstract val cmdId: CmdId
    abstract val entityId: ID
    abstract var sequence: RecordSequence?
    val recordedAt: RecordedAt = RecordedAt.now()

    fun sequence() =
        sequence ?: throw IllegalStateException("Records needs to be stored to db before it can be accessed")
}

interface RecordEntity<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    val id: ID
    val cmdId: CmdId
    val sequence: RecordSequence
    val recordedAt: RecordedAt
    fun apply(rec: RECORD): RecordEntity<ID, RECORD, OBJ>
    fun toDomainObject(): OBJ
}

interface CreateDomainObject<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    operator fun invoke(recs: List<RECORD>): OBJ
}