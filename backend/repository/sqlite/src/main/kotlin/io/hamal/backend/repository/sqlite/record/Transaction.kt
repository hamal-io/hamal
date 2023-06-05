package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.CreateDomainObject
import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.sqlite.internal.NamedPreparedStatementDelegate
import io.hamal.backend.repository.sqlite.internal.NamedPreparedStatementResultSetDelegate
import io.hamal.backend.repository.sqlite.internal.Transaction
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.base.DomainId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface RecordTransaction<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>> : Transaction {
    fun storeRecord(record: RECORD): RECORD
    fun recordsOf(id: ID): List<RECORD>
    fun lastRecordOf(id: ID): RECORD
    fun commandAlreadyApplied(id: ID, cmdId: CmdId): Boolean
    fun versionOf(id: ID, cmdId: CmdId): OBJ
    fun currentVersion(id: ID): OBJ
}

@OptIn(ExperimentalSerializationApi::class)
val protobuf = ProtoBuf {}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class SqliteRecordTransaction<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
    private val createDomainObject: CreateDomainObject<ID, RECORD, OBJ>,
    private val recordClass: KClass<RECORD>,
    private val delegate: Transaction,
    private val recordCache: RecordCache<ID, RECORD, OBJ>
) : RecordTransaction<ID, RECORD, OBJ> {

    override fun storeRecord(record: RECORD): RECORD {
        recordCache.add(this, record)

        execute(
            """
                INSERT INTO records
                    (cmd_id, entity_id,  data) 
                VALUES
                    (:cmdId, :entityId,  :data)
            """.trimIndent()
        ) {
            set("cmdId", record.cmdId)
            set("entityId", record.entityId)
            set("data", protobuf.encodeToByteArray(recordClass.serializer(), record))
        }

        return record
    }

    override fun recordsOf(id: ID): List<RECORD> {
        return recordCache.list(this, id)
    }

    override fun lastRecordOf(id: ID): RECORD {
        return recordCache.list(this, id).lastOrNull()
            ?: throw IllegalArgumentException("No records found for $id")
    }

    override fun commandAlreadyApplied(id: ID, cmdId: CmdId): Boolean {
        return recordsOf(id).any { it.cmdId == cmdId }
    }

    override fun versionOf(id: ID, cmdId: CmdId): OBJ {
        return createDomainObject(recordsOf(id).takeWhileInclusive { it.cmdId != cmdId })
    }

    override fun currentVersion(id: ID): OBJ {
        val lastRecord = lastRecordOf(id)
        return versionOf(id, lastRecord.cmdId)
    }

    override fun execute(sql: String) = delegate.execute(sql)

    override fun execute(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate) =
        delegate.execute(sql, block)

    override fun <T : Any> execute(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ) = delegate.execute(sql, block)

    override fun executeUpdate(sql: String) = delegate.executeUpdate(sql)
    override fun executeUpdate(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ) = delegate.executeUpdate(sql, block)

    override fun <T : Any> executeQuery(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ) = delegate.executeQuery(sql, block)

    override fun abort() = delegate.abort()
}
