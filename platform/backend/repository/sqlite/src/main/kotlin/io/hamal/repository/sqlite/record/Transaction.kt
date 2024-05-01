package io.hamal.repository.sqlite.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.sqlite.NamedPreparedStatementDelegate
import io.hamal.lib.sqlite.NamedPreparedStatementResultSetDelegate
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.record.*
import io.hamal.repository.record.RecordedAt.Companion.RecordedAt
import kotlin.reflect.KClass


class RecordTransactionSqlite<ID : ValueVariableSnowflakeId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
    private val createDomainObject: CreateDomainObject<ID, RECORD, OBJ>,
    private val recordClass: KClass<RECORD>,
    private val delegate: Transaction,
) : RecordRepository<ID, RECORD, OBJ>, Transaction {

    override fun store(record: RECORD): RECORD {
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
            set("data", json.serializeAndCompress(record))
        }

        return record
    }

    override fun recordsOf(id: ID): List<RECORD> {
        return executeQuery(
            """
            SELECT data, sequence, timestamp FROM records WHERE entity_id = :entityId ORDER BY sequence ASC
        """.trimIndent()
        ) {
            query {
                set("entityId", id)
            }
            map {
                json.decompressAndDeserialize(recordClass, it.getBytes("data")).also { record ->
                    record.recordSequence = RecordSequence(ValueNumber(it.getInt("sequence")))
                    record.recordedAt = RecordedAt(it.getInstant("timestamp"))
                }
            }
        }
    }

    override fun lastRecordOf(id: ID): RECORD {
        return executeQuery(
            """
            SELECT data, sequence, timestamp FROM records WHERE entity_id = :entityId ORDER BY sequence DESC LIMIT 1
        """.trimIndent()
        ) {
            query {
                set("entityId", id)
            }
            map {
                json.decompressAndDeserialize(recordClass, it.getBytes("data")).also { record ->
                    record.recordSequence = RecordSequence(ValueNumber(it.getInt("sequence")))
                    record.recordedAt = RecordedAt(it.getInstant("timestamp"))
                }
            }
        }.lastOrNull()
            ?: throw NoSuchElementException("${recordClass.simpleName!!.replace("Record", "")} not found")
    }

    override fun commandAlreadyApplied(cmdId: CmdId, id: ID): Boolean {
        return recordsOf(id).any { it.cmdId == cmdId }
    }

    override fun versionOf(id: ID, cmdId: CmdId): OBJ {
        return createDomainObject(recordsOf(id).takeWhileInclusive { it.cmdId != cmdId })
    }

    override fun versionOf(id: ID, sequence: RecordSequence): OBJ? {
        return executeQuery(
            """
            SELECT data, sequence, timestamp FROM records WHERE 
            entity_id = :entityId AND sequence <= :sequence ORDER BY sequence ASC
        """.trimIndent()
        ) {
            query {
                set("entityId", id)
                set("sequence", sequence.intValue)
            }
            map {
                json.decompressAndDeserialize(recordClass, it.getBytes("data")).also { record ->
                    record.recordSequence = RecordSequence(ValueNumber(it.getInt("sequence")))
                    record.recordedAt = RecordedAt(it.getInstant("timestamp"))
                }
            }
        }.ifEmpty { null }
            ?.let { records ->
                if (records.none { it.recordSequence == sequence }) {
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

    override fun clear() {
        delegate.execute("""DELETE FROM records""")
    }
}
