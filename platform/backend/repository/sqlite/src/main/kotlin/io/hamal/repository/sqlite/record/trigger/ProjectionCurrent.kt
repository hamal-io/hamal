package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.sqlite.hon
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<TriggerId, TriggerRecord, Trigger> {
    fun find(connection: Connection, triggerId: TriggerId): Trigger? {
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
                set("id", triggerId)
            }
            map { rs ->
                hon.decompressAndRead(Trigger::class, rs.getBytes("data"))
            }
        }
    }

    fun list(
        connection: Connection,
        query: TriggerQuery
    ): List<Trigger> {
        return connection.executeQuery<Trigger>(
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
                ${query.types()}
                ${query.topicIds()}
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
                hon.decompressAndRead(Trigger::class, rs.getBytes("data"))
            }
        }
    }

    fun count(
        connection: Connection,
        query: TriggerQuery
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
                ${query.funcIds()}
                ${query.types()}
                ${query.topicIds()}
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

    override fun upsert(tx: RecordTransactionSqlite<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, workspace_id, func_id, topic_id, namespace_id, type, data) 
                VALUES
                    (:id, :workspaceId, :funcId, :topicId, :namespaceId, :type, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("workspaceId", obj.workspaceId)
            set("funcId", obj.funcId)
            if (obj is Trigger.Event) {
                set("topicId", obj.topicId)
            } else {
                set("topicId", 0)
            }
            set("namespaceId", obj.namespaceId)
            set("type", obj.type.value)
            set("data", hon.writeAndCompress(obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 workspace_id   INTEGER NOT NULL,
                 func_id        INTEGER NOT NULL,
                 type           INTEGER NOT NULL,
                 topic_id       INTEGER NOT NULL,
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

    private fun TriggerQuery.types(): String {
        return if (types.isEmpty()) {
            ""
        } else {
            "AND type IN (${types.joinToString(",") { "'${it.stringValue}'" }})"
        }
    }

    private fun TriggerQuery.ids(): String {
        return if (triggerIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${triggerIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun TriggerQuery.workspaceIds(): String {
        return if (workspaceIds.isEmpty()) {
            ""
        } else {
            "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun TriggerQuery.funcIds(): String {
        return if (funcIds.isEmpty()) {
            ""
        } else {
            "AND func_id IN (${funcIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun TriggerQuery.topicIds(): String {
        return if (topicIds.isEmpty()) {
            ""
        } else {
            "AND topic_id IN (${topicIds.joinToString(",") { "${it.longValue}" }})"
        }
    }

    private fun TriggerQuery.namespaceIds(): String {
        return if (namespaceIds.isEmpty()) {
            ""
        } else {
            "AND namespace_id IN (${namespaceIds.joinToString(",") { "${it.longValue}" }})"
        }
    }
}
