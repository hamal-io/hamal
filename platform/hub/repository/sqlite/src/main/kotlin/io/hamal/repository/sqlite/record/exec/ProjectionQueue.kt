package io.hamal.repository.sqlite.record.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Exec
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionQueue : SqliteProjection<ExecId, ExecRecord, Exec> {

    fun pop(
        tx: SqliteRecordTransaction<ExecId, ExecRecord, Exec>,
        limit: Int
    ): List<Exec> {
        return tx.executeQuery(
            """
            DELETE FROM queue WHERE id IN (
                SELECT id FROM queue ORDER BY id LIMIT :limit
            ) RETURNING *
        """.trimIndent()
        ) {
            query {
                set("limit", limit)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Exec.serializer(), rs.getBytes("data"))
            }
        }
    }

    override fun upsert(
        tx: SqliteRecordTransaction<ExecId, ExecRecord, Exec>,
        obj: Exec
    ) {
        require(obj is io.hamal.repository.api.QueuedExec) { "exec not in status queued" }
        tx.execute(
            """
                INSERT INTO queue
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("data", protobuf.encodeToByteArray(Exec.serializer(), obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS queue (
                 id             INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM queue""")
    }

    override fun invalidate() {}
}
