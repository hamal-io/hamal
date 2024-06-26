package io.hamal.repository.sqlite.record.code

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.sqlite.Connection
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.sqlite.hon
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite.CurrentImpl<CodeId, CodeRecord, Code>() {

    fun find(connection: Connection, codeId: CodeId): Code? {
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
                set("id", codeId)
            }
            map { rs ->
                hon.decompressAndRead(Code::class, rs.getBytes("data"))
            }
        }
    }


    fun list(connection: Connection, query: CodeQuery): List<Code> {
        return connection.executeQuery<Code>(
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
                hon.decompressAndRead(Code::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: CodeQuery): Count {
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

    override fun upsert(tx: RecordTransactionSqlite<CodeId, CodeRecord, Code>, obj: Code) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, workspace_id, data) 
                VALUES
                    (:id, :workspaceId, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("workspaceId", obj.workspaceId)
            set("data", hon.writeAndCompress(obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 workspace_id       INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    private fun CodeQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun CodeQuery.ids(): String {
        return if (codeIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${codeIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}