package io.hamal.repository.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.value.ValueVariableSnowflakeId

interface RecordRepository<ID : ValueVariableSnowflakeId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun store(record: RECORD): RECORD
    fun recordsOf(id: ID): List<RECORD>
    fun lastRecordOf(id: ID): RECORD
    fun commandAlreadyApplied(cmdId: CmdId, id: ID): Boolean
    fun versionOf(id: ID, cmdId: CmdId): OBJ
    fun versionOf(id: ID, sequence: RecordSequence): OBJ?
    fun currentVersion(id: ID): OBJ
    fun clear()
}