package io.hamal.repository.sqlite.record.workspace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import io.hamal.repository.record.serde
import io.hamal.repository.record.workspace.WorkspaceRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException


internal object ProjectionCurrent : ProjectionSqlite<WorkspaceId, WorkspaceRecord, Workspace> {
    override fun upsert(tx: RecordTransactionSqlite<WorkspaceId, WorkspaceRecord, Workspace>, obj: Workspace) {
        try {
            tx.execute(
                """
                INSERT OR REPLACE INTO current
                    (id, creator_id, data) 
                VALUES
                    (:id, :creatorId, :data)
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("creatorId", obj.creatorId)
                set("data", serde.writeAndCompress(obj))
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("UNIQUE constraint failed: current.name)")) {
                throw IllegalArgumentException("${obj.name} already exists")
            }
            throw e
        }
    }

    private fun find(
        tx: RecordTransactionSqlite<WorkspaceId, WorkspaceRecord, Workspace>,
        workspaceName: WorkspaceName
    ): Workspace? {
        return tx.executeQueryOne(
            """
                SELECT 
                    name
                FROM
                    current
                WHERE
                    name = :name
            """.trimIndent()
        ) {
            query {
                set("name", workspaceName)
            }
            map { rs ->
                serde.decompressAndRead(Workspace::class, rs.getBytes("data"))
            }
        }
    }


    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 creator_id     INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    fun find(connection: Connection, workspaceId: WorkspaceId): Workspace? {
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
                set("id", workspaceId)
            }
            map { rs ->
                serde.decompressAndRead(Workspace::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: WorkspaceQuery): List<Workspace> {
        return connection.executeQuery<Workspace>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.accountIds()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                serde.decompressAndRead(Workspace::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: WorkspaceQuery): Count {
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
                ${query.accountIds()}
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

    private fun WorkspaceQuery.ids(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun WorkspaceQuery.accountIds(): String {
        return if (accountIds.isEmpty()) {
            ""
        } else {
            "AND creator_id IN (${accountIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}
