package io.hamal.repository.sqlite.record.topic

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.record.json
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<TopicId, TopicRecord, Topic> {
    fun find(connection: Connection, topicId: TopicId): Topic? {
        return connection.executeQueryOne(
            """
            SELECT 
                data
             FROM
                current
            WHERE id  = :id
        """.trimIndent()
        ) {
            query {
                set("id", topicId)
            }
            map { rs ->
                json.decompressAndDeserialize(Topic::class, rs.getBytes("data"))
            }
        }
    }

    fun find(connection: Connection, namespaceId: NamespaceId, topicName: TopicName): Topic? {
        return connection.executeQueryOne(
            """
            SELECT 
                data
             FROM
                current
            WHERE namespace_id = :namespaceId and name = :topicName
        """.trimIndent()
        ) {
            query {
                set("namespaceId", namespaceId)
                set("topicName", topicName)
            }
            map { rs ->
                json.decompressAndDeserialize(Topic::class, rs.getBytes("data"))
            }
        }
    }

    fun list(
        connection: Connection,
        query: TopicQuery
    ): List<Topic> {
        return connection.executeQuery<Topic>(
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
                ${query.types()}
                ${query.names()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                json.decompressAndDeserialize(Topic::class, rs.getBytes("data"))
            }
        }
    }

    fun count(
        connection: Connection,
        query: TopicQuery
    ): Count {
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
                ${query.types()}
                ${query.names()}
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

    override fun upsert(tx: RecordTransactionSqlite<TopicId, TopicRecord, Topic>, obj: Topic) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, workspace_id, namespace_id, type, name, data) 
                VALUES
                    (:id, :workspaceId, :namespaceId, :type, :name, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("workspaceId", obj.workspaceId)
            set("namespaceId", obj.namespaceId)
            set("type", obj.type.value)
            set("name", obj.name)
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
                 type           INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
        );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    private fun TopicQuery.types(): String {
        return if (types.isEmpty()) {
            ""
        } else {
            "AND type IN (${types.joinToString(",") { "${it.value}" }})"
        }
    }

    private fun TopicQuery.ids(): String {
        return if (topicIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${topicIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun TopicQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun TopicQuery.namespaceIds(): String {
        return if (namespaceIds.isEmpty()) {
            ""
        } else {
            "AND namespace_id IN (${namespaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }


    private fun TopicQuery.names(): String {
        return if (names.isEmpty()) {
            ""
        } else {
            "AND name IN (${names.joinToString(",") { "'${it.value}'" }})"
        }
    }
}
