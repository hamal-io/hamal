package io.hamal.backend.repository.record

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.base.DomainId

interface RecordType {
    val value: Int
}

data class RecordSequence(val value: Int) : Comparable<RecordSequence>//FIXME becomes VO
{
    companion object {
        fun first() = RecordSequence(1)
    }

    override fun compareTo(other: RecordSequence) = other.value.compareTo(value)

    fun next() = RecordSequence(value + 1)
}

interface Record<ID : DomainId, TYPE : RecordType> {
    val id: ID
    val type: TYPE
    val cmdId: CmdId
    val prevCmdId: CmdId
    val sequence: RecordSequence
    // FIXME sval recordedAt: Instant
}

interface RecordEntity<ID : DomainId, RECORD : Record<ID, *>> {
    val id: ID
    val cmdId: CmdId
    val sequence: RecordSequence

    fun apply(rec: RECORD): RecordEntity<ID, RECORD>
}