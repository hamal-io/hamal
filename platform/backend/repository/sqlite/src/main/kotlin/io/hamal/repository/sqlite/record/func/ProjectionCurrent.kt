package io.hamal.repository.sqlite.record.func

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.record.serde
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<FuncId, FuncRecord, Func> {

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
                serde.decompressAndRead(Func::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: FuncQuery): List<Func> {
        return connection.executeQuery<Func>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.workspaceIds()}
                ${query.namespaceIds()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                serde.decompressAndRead(Func::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: FuncQuery): Count {
        return Count(
            connection.executeQueryOne(
                """
            SELECT 
                COUNT(*) as count 
            FROM 
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.workspaceIds()}
                ${query.namespaceIds()}
        """.trimIndent()
            ) {
                query {
                    set("afterId", query.afterId)
                }
                map {
                    it.getLong("count")
                }
            } ?: 0L
        )
    }

    override fun upsert(tx: RecordTransactionSqlite<FuncId, FuncRecord, Func>, obj: Func) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, workspace_id, namespace_id, data) 
                VALUES
                    (:id, :workspaceId, :namespaceId, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("workspaceId", obj.workspaceId)
            set("namespaceId", obj.namespaceId)
            set("data", serde.writeAndCompress(obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 workspace_id       INTEGER NOT NULL,
                 namespace_id        INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    private fun FuncQuery.ids(): String {
        return if (funcIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${funcIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun FuncQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun FuncQuery.namespaceIds(): String {
        return if (namespaceIds.isEmpty()) {
            ""
        } else {
            "AND namespace_id IN (${namespaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}
