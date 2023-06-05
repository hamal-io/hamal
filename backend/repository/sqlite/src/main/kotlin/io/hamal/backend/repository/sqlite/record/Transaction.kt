package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.backend.repository.sqlite.internal.NamedPreparedStatementDelegate
import io.hamal.backend.repository.sqlite.internal.NamedPreparedStatementResultSetDelegate
import io.hamal.backend.repository.sqlite.internal.Transaction
import io.hamal.lib.domain.vo.base.DomainId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface RecordTransaction<ID : DomainId, RECORD : Record<ID>> : Transaction {
    fun storeRecord(record: RECORD)
    fun allRecords(id: ID): List<RECORD>
    fun recordsUntilInclusive(id: ID, seq: RecordSequence): List<RECORD>
    fun lastRecord(id: ID): RECORD
}

@OptIn(ExperimentalSerializationApi::class)
val protobuf = ProtoBuf {}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class SqliteRecordTransaction<ID : DomainId, RECORD : Record<ID>>(
    private val recordClass: KClass<RECORD>,
    private val delegate: Transaction
) : RecordTransaction<ID, RECORD> {

    override fun storeRecord(record: RECORD) {
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
    }

    override fun allRecords(id: ID): List<RECORD> {
        return executeQuery(
            """
        SELECT 
            sequence,
            data
        FROM records 
            WHERE entity_id = :entityId 
        ORDER BY sequence;
    """.trimIndent()
        ) {
            query {
                set("entityId", id)
            }
            map { rs ->
                protobuf.decodeFromByteArray(recordClass.serializer(), rs.getBytes("data")).apply {
                    sequence = RecordSequence(rs.getInt("sequence"))
                }
            }
        }
    }

    override fun recordsUntilInclusive(id: ID, seq: RecordSequence): List<RECORD> {
        return executeQuery(
            """
        SELECT 
            data
        FROM records 
            WHERE entity_id = :entityId AND sequence <= :sequence
        ORDER BY sequence;
    """.trimIndent()
        ) {
            query {
                set("entityId", id)
                set("sequence", seq.value)
            }
            map { rs -> protobuf.decodeFromByteArray(recordClass.serializer(), rs.getBytes("data")) }
        }
    }

    override fun lastRecord(id: ID): RECORD {
        return executeQueryOne(
            """
        SELECT * FROM
             records
         WHERE
             entity_id = :entityId
        ORDER BY sequence DESC
        LIMIT 1
    """.trimIndent()
        ) {
            query {
                set("entityId", id)
            }
            map { rs -> protobuf.decodeFromByteArray(recordClass.serializer(), rs.getBytes("data")) }
        } ?: throw IllegalArgumentException("No records found for $id")
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
