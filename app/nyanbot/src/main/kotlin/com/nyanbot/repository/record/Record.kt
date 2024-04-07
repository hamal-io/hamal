package com.nyanbot.repository.record

import io.hamal.lib.common.domain.*
import io.hamal.lib.common.util.TimeUtils
import java.time.Instant

class RecordClass(override val value: String) : ValueObjectString()

class RecordSequence(override val value: Int) : ValueObjectInt() {
    companion object {
        fun first() = RecordSequence(1)
    }

    fun next() = RecordSequence(value + 1)
}

class RecordedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): RecordedAt = RecordedAt(TimeUtils.now())
    }

    fun toUpdatedAt() = UpdatedAt(value)
}


abstract class Record<ID : ValueObjectId> {
    abstract val entityId: ID
    abstract var recordSequence: RecordSequence?
    abstract var recordedAt: RecordedAt?
    val recordClass: RecordClass = RecordClass(this::class.simpleName!!)

    fun sequence() =
        recordSequence ?: throw IllegalStateException("Records needs to be stored to db before it can be accessed")

    fun recordedAt() =
        recordedAt ?: throw IllegalStateException("Records needs to be stored to db before it can be accessed")
}

interface RecordEntity<ID : ValueObjectId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    val id: ID
    val sequence: RecordSequence
    val recordedAt: RecordedAt
    fun apply(rec: RECORD): RecordEntity<ID, RECORD, OBJ>
    fun toDomainObject(): OBJ
}

interface CreateDomainObject<ID : ValueObjectId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    operator fun invoke(recs: List<RECORD>): OBJ
}