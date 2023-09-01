package io.hamal.repository.memory.record

import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import io.hamal.lib.common.domain.DomainId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class BaseRecordRepository<ID : DomainId, RECORD : Record<ID>> {

    private val lock = ReentrantLock()
    private val store = mutableMapOf<ID, MutableList<RECORD>>()

    fun addRecord(record: RECORD) {
        lock.withLock {
            val records = store.getOrDefault(record.entityId, mutableListOf())
            record.apply { sequence = RecordSequence(records.size + 1) }
            store[record.entityId] = records.apply { add(record) }
        }
    }

    fun listRecords(id: ID): List<RECORD> = lock.withLock { store.getOrDefault(id, listOf()) }
    fun contains(id: ID) = lock.withLock { store.containsKey(id) }

    open fun clear() {
        lock.withLock {
            store.clear()
        }
    }
}