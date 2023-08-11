package io.hamal.backend.repository.sqlite.record.func

import io.hamal.backend.repository.api.Func
import io.hamal.backend.repository.record.func.FuncRecord
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.backend.repository.sqlite.record.protobuf
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.Connection
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : Projection<FuncId, FuncRecord, Func> {

    fun find(connection: Connection, funcId: FuncId): Func? {
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
                set("id", funcId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Func.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, afterId: FuncId, limit: Limit): List<Func> {
        return connection.executeQuery<Func>(
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
    }
}
