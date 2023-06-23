package io.hamal.backend.repository.sqlite.record.exec

import io.hamal.backend.repository.api.domain.Exec
import io.hamal.backend.repository.record.exec.ExecRecord
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.backend.repository.sqlite.record.protobuf
import io.hamal.lib.common.DefaultLruCache
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : Projection<ExecId, ExecRecord, Exec> {
    internal val lruCache = DefaultLruCache<ExecId, Exec>(10_000)
    fun find(connection: Connection, execId: ExecId): Exec? {
        return lruCache.computeIfAbsent(execId) {
            connection.executeQueryOne(
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
                    set("id", it)
                }
                map { rs ->
                    protobuf.decodeFromByteArray(Exec.serializer(), rs.getBytes("data"))
                }
            }
        }
    }

    fun list(connection: Connection, afterId: ExecId, limit: Int): List<Exec> {
        return connection.executeQuery<Exec>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id > :afterId
            ORDER BY id ASC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", afterId)
                set("limit", limit)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Exec.serializer(), rs.getBytes("data"))
            }
        }
    }

    override fun update(tx: RecordTransaction<ExecId, ExecRecord, Exec>, obj: Exec) {
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
            set("data", protobuf.encodeToByteArray(Exec.serializer(), obj))
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
        lruCache.clear()
    }
}
