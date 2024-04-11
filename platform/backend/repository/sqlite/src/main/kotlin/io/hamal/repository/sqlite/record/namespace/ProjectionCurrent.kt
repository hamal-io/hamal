package io.hamal.repository.sqlite.record.namespace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.record.json
import io.hamal.repository.record.namespace.NamespaceRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<NamespaceId, NamespaceRecord, Namespace> {

    fun find(connection: Connection, namespaceId: NamespaceId): Namespace? {
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
                set("id", namespaceId)
            }
            map { rs ->
                json.decompressAndDeserialize(Namespace::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: NamespaceQuery): List<Namespace> {
        return connection.executeQuery<Namespace>(
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
                json.decompressAndDeserialize(Namespace::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: NamespaceQuery): Count {
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

    override fun upsert(tx: RecordTransactionSqlite<NamespaceId, NamespaceRecord, Namespace>, obj: Namespace) {
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
            set("data", json.serializeAndCompress(obj))
        }
    }

    fun delete(tx: RecordTransactionSqlite<NamespaceId, NamespaceRecord, Namespace>, obj: Namespace) {
        tx.execute(
            """
                DELETE FROM 
                    current
                WHERE      
                    id = :id
            """.trimIndent()
        ) {
            set("id", obj.id)
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 workspace_id   INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    private fun NamespaceQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun NamespaceQuery.ids(): String {
        return if (namespaceIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${namespaceIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
