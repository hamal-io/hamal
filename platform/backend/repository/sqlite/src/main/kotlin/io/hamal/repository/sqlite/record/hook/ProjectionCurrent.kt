package io.hamal.repository.sqlite.record.hook

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.record.hook.HookRecord
import io.hamal.repository.record.json
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<HookId, HookRecord, Hook> {

    fun find(connection: Connection, hookId: HookId): Hook? {
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
                set("id", hookId)
            }
            map { rs ->
                json.decompressAndDeserialize(Hook::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: HookQuery): List<Hook> {
        return connection.executeQuery<Hook>(
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
                json.decompressAndDeserialize(Hook::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: HookQuery): Count {
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

    override fun upsert(tx: RecordTransactionSqlite<HookId, HookRecord, Hook>, obj: Hook) {
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
            set("data", json.serializeAndCompress(obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 workspace_id       INTEGER NOT NULL,
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

    private fun HookQuery.ids(): String {
        return if (hookIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${hookIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun HookQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun HookQuery.namespaceIds(): String {
        return if (namespaceIds.isEmpty()) {
            ""
        } else {
            "AND namespace_id IN (${namespaceIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
