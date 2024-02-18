package io.hamal.repository.sqlite.record.namespace_tree

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.api.NamespaceTreeQueryRepository
import io.hamal.repository.record.json
import io.hamal.repository.record.namespace_tree.NamespaceTreeRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree> {

    fun find(connection: Connection, namespaceId: NamespaceId): NamespaceTree? {
        return connection.executeQueryOne(
            """
            SELECT
                data
             FROM
                current
            WHERE
                id  = (
                    SELECT tree_id FROM namespaces WHERE namespace_id = :namespaceId
                )
        """.trimIndent()
        ) {
            query {
                set("namespaceId", namespaceId)
            }
            map { rs ->
                json.decompressAndDeserialize(NamespaceTree::class, rs.getBytes("data"))
            }
        }
    }


    fun list(connection: Connection, query: NamespaceTreeQueryRepository.NamespaceTreeQuery): List<NamespaceTree> {
        return connection.executeQuery<NamespaceTree>(
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
                json.decompressAndDeserialize(NamespaceTree::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: NamespaceTreeQueryRepository.NamespaceTreeQuery): Count {
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


    override fun upsert(
        tx: RecordTransactionSqlite<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree>,
        obj: NamespaceTree
    ) {
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

        obj.root.preorder().forEach { namespaceId ->
            tx.execute(
                """
                INSERT OR REPLACE INTO namespaces
                    (namespace_id, tree_id) 
                VALUES
                    (:namespaceId, :treeId)
            """.trimIndent()
            ) {
                set("namespaceId", namespaceId)
                set("treeId", obj.id)
            }
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

        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS namespaces (
                 namespace_id   INTEGER NOT NULL,
                 tree_id        INTEGER NOT NULL,
                 PRIMARY KEY    (namespace_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    private fun NamespaceTreeQueryRepository.NamespaceTreeQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun NamespaceTreeQueryRepository.NamespaceTreeQuery.ids(): String {
        return if (treeIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${treeIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
