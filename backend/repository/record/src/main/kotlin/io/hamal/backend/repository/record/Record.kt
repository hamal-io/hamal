package io.hamal.backend.repository.record

import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.base.DomainId

interface RecordType {
    val value: Int
}

data class RecordSequence(val value: Int) : Comparable<RecordSequence>//FIXME becomes VO
{
    override fun compareTo(other: RecordSequence) = other.value.compareTo(value)
}

interface Record<ID : DomainId, TYPE : RecordType> {
    val id: ID
    val type: TYPE
    val commandId: CommandId
    val previousCommandId: CommandId
    val sequence: RecordSequence
}

interface RecordEntity<ID : DomainId> {
    val id: ID
    val commandId: CommandId
    val sequence: RecordSequence
}