package io.hamal.repository.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.UpdatedAt
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.*
import io.hamal.repository.record.RecordClass.Companion.RecordClass
import java.time.Instant

class RecordClass(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun RecordClass(value: String) = RecordClass(ValueString(value))
    }
}

class RecordSequence(override val value: ValueNumber) : ValueVariableNumber() {
    companion object {
        fun first() = RecordSequence(1)
        fun RecordSequence(value: Int) = RecordSequence(ValueNumber(value))
    }

    fun next() = RecordSequence(value.intValue + 1)
}

class RecordedAt(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        @JvmStatic
        fun now(): RecordedAt = RecordedAt(TimeUtils.now())
        fun RecordedAt(value: Instant) = RecordedAt(ValueInstant(value))
    }

    fun toUpdatedAt() = UpdatedAt(value)
}


abstract class Record<ID : ValueVariableSnowflakeId> {
    abstract val cmdId: CmdId
    abstract val entityId: ID
    abstract var recordSequence: RecordSequence?
    abstract var recordedAt: RecordedAt?
    val recordClass: RecordClass = RecordClass(this::class.simpleName!!)

    fun sequence() =
        recordSequence ?: throw IllegalStateException("Records needs to be stored to db before it can be accessed")

    fun recordedAt() =
        recordedAt ?: throw IllegalStateException("Records needs to be stored to db before it can be accessed")
}

interface RecordEntity<ID : ValueVariableSnowflakeId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    val id: ID
    val cmdId: CmdId
    val sequence: RecordSequence
    val recordedAt: RecordedAt
    fun apply(rec: RECORD): RecordEntity<ID, RECORD, OBJ>
    fun toDomainObject(): OBJ
}

interface CreateDomainObject<ID : ValueVariableSnowflakeId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    operator fun invoke(recs: List<RECORD>): OBJ
}