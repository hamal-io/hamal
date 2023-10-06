package io.hamal.repository.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainObject

interface RecordRepository<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun store(record: RECORD): RECORD
    fun recordsOf(id: ID): List<RECORD>
    fun lastRecordOf(id: ID): RECORD
    fun commandAlreadyApplied(cmdId: CmdId, id: ID): Boolean
    fun versionOf(id: ID, cmdId: CmdId): OBJ
    fun currentVersion(id: ID): OBJ
    fun clear()
}