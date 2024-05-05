package io.hamal.repository.sqlite.record.exec

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.sqlite.hon
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<ExecId, ExecRecord, Exec> {
    fun find(connection: Connection, execId: ExecId): Exec? {
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
                hon.decompressAndRead(Exec::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: ExecQuery): List<Exec> {
        return connection.executeQuery<Exec>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.workspaceIds()}
                ${query.funcIds()}
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
                hon.decompressAndRead(Exec::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: ExecQuery): Count {
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
                ${query.funcIds()}
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


    override fun upsert(
        tx: RecordTransactionSqlite<ExecId, ExecRecord, Exec>, obj: Exec
    ) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    ( id, status, namespace_id, workspace_id, func_id, data) 
                VALUES
                    ( :id, :status, :namespaceId, :workspaceId, :funcId, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("status", obj.status)
            set("namespaceId", obj.namespaceId)
            set("workspaceId", obj.workspaceId)
            set("funcId", obj.correlation?.funcId ?: FuncId(0))
            set("data", hon.writeAndCompress(obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 status         INTEGER NOT NULL,
                 workspace_id   INTEGER NOT NULL,
                 func_id        INTEGER NOT NULL,
                 namespace_id   INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    private fun ExecQuery.ids(): String {
        return if (execIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${execIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun ExecQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun ExecQuery.funcIds(): String {
        return if (funcIds.isEmpty()) {
            ""
        } else {
            "AND func_id IN (${funcIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun ExecQuery.namespaceIds(): String {
        return if (namespaceIds.isEmpty()) {
            ""
        } else {
            "AND namespace_id IN (${namespaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}
