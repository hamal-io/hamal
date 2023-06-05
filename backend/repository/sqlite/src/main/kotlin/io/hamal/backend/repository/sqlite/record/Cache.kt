package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.Record
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.vo.base.DomainId

class RecordCache<ID : DomainId, RECORD : Record<ID>>(
    private val recordLoader: RecordLoader<ID, RECORD>
) {
    private val store = KeyedOnce.lru<ID, MutableList<RECORD>>(10_000)

    fun add(tx: RecordTransaction<ID, RECORD>, record: RECORD) {
        records(tx, record.entityId).add(record)
    }

    fun list(tx: RecordTransaction<ID, RECORD>, entityId: ID): List<RECORD> {
        return records(tx, entityId).toList()
    }

    fun invalidate() {
        store.clear()
    }

    private fun records(tx: RecordTransaction<ID, RECORD>, entityId: ID): MutableList<RECORD> {
        return store(entityId) { recordLoader.loadAll(tx, it).toMutableList() }
    }

}