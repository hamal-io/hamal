package io.hamal.repository.sqlite.record.exec

import io.hamal.repository.api.Exec
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.sqlite.record.Projection
import io.hamal.repository.sqlite.record.RecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.Connection
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : Projection<ExecId, ExecRecord, io.hamal.repository.api.Exec> {
    fun find(connection: Connection, execId: ExecId): io.hamal.repository.api.Exec? {
        return connection.executeQueryOne(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id  = :id
        """.trimIndent()
        ) {
            query {
                set("id", execId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(io.hamal.repository.api.Exec.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, afterId: ExecId, limit: Limit): List<io.hamal.repository.api.Exec> {
        return connection.executeQuery<io.hamal.repository.api.Exec>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", afterId)
                set("limit", limit)
            }
            map { rs ->
                protobuf.decodeFromByteArray(io.hamal.repository.api.Exec.serializer(), rs.getBytes("data"))
            }
        }
    }

    override fun upsert(tx: RecordTransaction<ExecId, ExecRecord, io.hamal.repository.api.Exec>, obj: io.hamal.repository.api.Exec) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id,status, data) 
                VALUES
                    (:id,:status, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("status", obj.status.value)
            set("data", protobuf.encodeToByteArray(io.hamal.repository.api.Exec.serializer(), obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 status         INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(connection: Connection) {
        connection.execute("""DELETE FROM current""")
    }

    override fun invalidate() {
    }
}
