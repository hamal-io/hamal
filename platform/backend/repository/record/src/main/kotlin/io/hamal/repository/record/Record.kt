package io.hamal.repository.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.domain.vo.RecordedAt

data class RecordSequence(val value: Int) : Comparable<RecordSequence>//FIXME becomes VO
{
    companion object {
        fun first() = RecordSequence(1)
    }

    override fun compareTo(other: RecordSequence) = other.value.compareTo(value)

    fun next() = RecordSequence(value + 1)
}

abstract class Record<ID : ValueObjectId> {
    abstract val cmdId: CmdId
    abstract val entityId: ID
    abstract var sequence: RecordSequence?
    abstract var recordedAt: RecordedAt?

    fun sequence() =
        sequence ?: throw IllegalStateException("Records needs to be stored to db before it can be accessed")

    fun recordedAt() =
        recordedAt ?: throw IllegalStateException("Records needs to be stored to db before it can be accessed")
}

interface RecordEntity<ID : ValueObjectId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    val id: ID
    val cmdId: CmdId
    val sequence: RecordSequence
    val recordedAt: RecordedAt
    fun apply(rec: RECORD): RecordEntity<ID, RECORD, OBJ>
    fun toDomainObject(): OBJ
}

interface CreateDomainObject<ID : ValueObjectId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    operator fun invoke(recs: List<RECORD>): OBJ
}