package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.base.DomainId

class RecordCache<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
    private val recordLoader: RecordLoader<ID, RECORD>
) {
    private val store = KeyedOnce.lru<ID, MutableList<RECORD>>(10_000)

    fun add(tx: RecordTransaction<ID, RECORD, OBJ>, record: RECORD) {
        val records = records(tx, record.entityId)
        records.add(record.apply { sequence = sequence ?: RecordSequence(records.size + 1) })
    }

    fun list(tx: RecordTransaction<ID, RECORD, OBJ>, entityId: ID): List<RECORD> {
        return records(tx, entityId).toList()
    }

    fun invalidate() {
        store.clear()
    }

    private fun records(tx: RecordTransaction<ID, RECORD, OBJ>, entityId: ID): MutableList<RECORD> {
        return store(entityId) { recordLoader.loadAll(tx, it).toMutableList() }
    }

}