package com.nyanbot.repository.record

import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.ValueObjectId

interface RecordRepository<ID : ValueObjectId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun store(record: RECORD): RECORD
    fun recordsOf(id: ID): List<RECORD>
    fun lastRecordOf(id: ID): RECORD
    fun versionOf(id: ID, sequence: RecordSequence): OBJ?
    fun currentVersion(id: ID): OBJ
    fun clear()
}