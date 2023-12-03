package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.RecordedAt
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordRepository
import io.hamal.repository.record.RecordSequence
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

abstract class RecordMemoryRepository<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
    private val createDomainObject: CreateDomainObject<ID, RECORD, OBJ>,
    private val recordClass: KClass<RECORD>,
) : RecordRepository<ID, RECORD, OBJ> {

    private val lock = ReentrantLock()
    private val store = mutableMapOf<ID, MutableList<RECORD>>()

    override fun store(record: RECORD): RECORD {
        return lock.withLock {
            val records = store.getOrDefault(record.entityId, mutableListOf())
            record.apply {
                sequence = RecordSequence(records.size + 1)
                recordedAt = RecordedAt.now()
            }
            store[record.entityId] = records.apply { add(record) }
            store[record.entityId]!!.last()
        }
    }

    override fun recordsOf(id: ID): List<RECORD> {
        return lock.withLock { store.getOrDefault(id, listOf()) }
    }

    override fun lastRecordOf(id: ID): RECORD {
        return recordsOf(id).lastOrNull()
            ?: throw NoSuchElementException("${recordClass.simpleName!!.replace("Record", "")} not found")

    }

    override fun commandAlreadyApplied(cmdId: CmdId, id: ID): Boolean {
        return recordsOf(id).any { it.cmdId == cmdId }
    }

    override fun versionOf(id: ID, cmdId: CmdId): OBJ {
        return createDomainObject(recordsOf(id).takeWhileInclusive { it.cmdId != cmdId })
    }

    override fun versionOf(id: ID, sequence: RecordSequence): OBJ? {
        return recordsOf(id)
            .filter {
                val seq = it.sequence
                seq != null && seq >= sequence
            }
            .ifEmpty { null }
            ?.let { records ->
                if (records.none { it.sequence == sequence }) {
                    null
                } else {
                    createDomainObject(records)
                }
            }
    }

    override fun currentVersion(id: ID): OBJ {
        val lastRecord = lastRecordOf(id)
        return versionOf(id, lastRecord.cmdId)
    }

    override fun clear() {
        lock.withLock {
            store.clear()
        }
    }
}