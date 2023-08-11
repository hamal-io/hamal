package io.hamal.backend.repository.sqlite.record.exec

import io.hamal.backend.repository.api.Exec
import io.hamal.backend.repository.api.QueuedExec
import io.hamal.backend.repository.record.exec.ExecRecord
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.backend.repository.sqlite.record.protobuf
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.Connection
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionQueue : Projection<ExecId, ExecRecord, Exec> {

    fun pop(tx: RecordTransaction<ExecId, ExecRecord, Exec>, limit: Int): List<Exec> {
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

    override fun update(tx: RecordTransaction<ExecId, ExecRecord, Exec>, obj: Exec) {
        require(obj is QueuedExec) { "exec not in status queued" }
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

    override fun clear(connection: Connection) {
        connection.execute("""DELETE FROM queue""")
    }

    override fun invalidate() {}
}
