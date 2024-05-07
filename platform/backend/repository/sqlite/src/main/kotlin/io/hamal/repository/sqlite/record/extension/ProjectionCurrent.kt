package io.hamal.repository.sqlite.record.extension

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.record.extension.ExtensionRecord
import io.hamal.repository.sqlite.hon
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionCurrent : ProjectionSqlite<ExtensionId, ExtensionRecord, Extension> {
    override fun upsert(tx: RecordTransactionSqlite<ExtensionId, ExtensionRecord, Extension>, obj: Extension) {
        try {
            tx.execute(
                """
                INSERT INTO current
                    (id, workspace_id, name, data) 
                VALUES
                    (:id, :workspaceId, :name, :data)
                ON CONFLICT(id) DO UPDATE 
                        SET name= :name, data= :data;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("workspaceId", obj.workspaceId)
                set("name", obj.name)
                set("data", hon.writeAndCompress(obj))
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: current.workspace_id, current.name)")) {
                throw IllegalArgumentException("${obj.name} already exists in workspace ${obj.workspaceId}")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 workspace_id       INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (workspace_id, name)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    fun find(connection: Connection, extensionId: ExtensionId): Extension? {
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
                set("id", extensionId)
            }
            map { rs ->
                hon.decompressAndRead(Extension::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: ExtensionQuery): List<Extension> {
        return connection.executeQuery<Extension>(
            """
            SELECT 
                data
            FROM
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.workspaceIds()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                hon.decompressAndRead(Extension::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: ExtensionQuery): Count {
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

    private fun ExtensionQuery.ids(): String {
        return if (extensionIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${extensionIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun ExtensionQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}