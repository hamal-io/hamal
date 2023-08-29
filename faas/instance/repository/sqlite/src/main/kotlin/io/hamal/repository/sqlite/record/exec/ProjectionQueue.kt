package io.hamal.repository.sqlite.record.exec

import io.hamal.repository.api.Exec
import io.hamal.repository.api.QueuedExec
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.sqlite.record.Projection
import io.hamal.repository.sqlite.record.RecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.Connection
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionQueue : Projection<ExecId, ExecRecord, io.hamal.repository.api.Exec> {

    fun pop(tx: RecordTransaction<ExecId, ExecRecord, io.hamal.repository.api.Exec>, limit: Int): List<io.hamal.repository.api.Exec> {
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
                protobuf.decodeFromByteArray(io.hamal.repository.api.Exec.serializer(), rs.getBytes("data"))
            }
        }
    }

    override fun upsert(tx: RecordTransaction<ExecId, ExecRecord, io.hamal.repository.api.Exec>, obj: io.hamal.repository.api.Exec) {
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
            set("data", protobuf.encodeToByteArray(io.hamal.repository.api.Exec.serializer(), obj))
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

    override fun clear(connection: Connection) {
        connection.execute("""DELETE FROM queue""")
    }

    override fun invalidate() {}
}
