package io.hamal.repository.sqlite.record.snippet

import io.hamal.lib.domain.vo.SnippetId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Snippet
import io.hamal.repository.api.SnippetQueryRepository.SnippetQuery
import io.hamal.repository.record.snippet.SnippetRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
object ProjectionCurrent : SqliteProjection<SnippetId, SnippetRecord, Snippet> {


    override fun upsert(tx: SqliteRecordTransaction<SnippetId, SnippetRecord, Snippet>, obj: Snippet) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, group_id, data) 
                VALUES
                    (:id, :groupId, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("groupId", obj.groupId)
            set("data", protobuf.encodeToByteArray(Snippet.serializer(), obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 group_id       INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    fun find(connection: Connection, snippetId: SnippetId): Snippet? {
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
                set("id", snippetId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Snippet.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: SnippetQuery): List<Snippet> {
        return connection.executeQuery<Snippet>(
            """
            SELECT 
                data
             FROM
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.groupIds()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Snippet.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: SnippetQuery): ULong {
        return connection.executeQueryOne(
            """
            SELECT 
                COUNT(*) as count 
            FROM 
                current
            WHERE
                id < :afterId
                ${query.ids()}
                ${query.groupIds()}
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
            }
            map {
                it.getLong("count").toULong()
            }
        } ?: 0UL
    }

    private fun SnippetQuery.ids(): String {
        return if (snippetIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${snippetIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun SnippetQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}