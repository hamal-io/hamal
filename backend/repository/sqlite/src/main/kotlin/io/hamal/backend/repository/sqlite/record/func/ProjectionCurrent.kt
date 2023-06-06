package io.hamal.backend.repository.sqlite.record.func

import io.hamal.backend.repository.api.domain.Func
import io.hamal.backend.repository.record.func.FuncRecord
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.backend.repository.sqlite.record.protobuf
import io.hamal.lib.common.DefaultLruCache
import io.hamal.lib.domain.vo.FuncId
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : Projection<FuncId, FuncRecord, Func> {
    internal val lruCache = DefaultLruCache<FuncId, Func>(10_000)
    fun find(connection: Connection, funcId: FuncId): Func? {
        return lruCache.computeIfAbsent(funcId) {
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
                    protobuf.decodeFromByteArray(Func.serializer(), rs.getBytes("data"))
                }
            }
        }
    }

    fun list(connection: Connection, afterId: FuncId, limit: Int): List<Func> {
        return connection.executeQuery<Func>(
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
                protobuf.decodeFromByteArray(Func.serializer(), rs.getBytes("data"))
            }
        }
    }

    override fun update(tx: RecordTransaction<FuncId, FuncRecord, Func>, obj: Func) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("data", protobuf.encodeToByteArray(Func.serializer(), obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
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
