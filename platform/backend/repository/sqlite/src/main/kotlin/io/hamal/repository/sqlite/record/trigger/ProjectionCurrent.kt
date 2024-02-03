package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.HookTrigger
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.record.json
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
                json.decompressAndDeserialize(Trigger::class, rs.getBytes("data"))
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
                ${query.groupIds()}
                ${query.funcIds()}
                ${query.types()}
                ${query.topicIds()}
                ${query.hookIds()}
                ${query.flowIds()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                json.decompressAndDeserialize(Trigger::class, rs.getBytes("data"))
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
                ${query.groupIds()}
                ${query.funcIds()}
                ${query.types()}
                ${query.topicIds()}
                ${query.hookIds()}
                ${query.flowIds()}
        """.trimIndent()
            ) {
                query {
                    set("afterId", query.afterId)
                }
                map {
                    it.getLong("count")
                }
            } ?: 0
        )
    }

    override fun upsert(tx: RecordTransactionSqlite<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, group_id, func_id, topic_id, hook_id, flow_id, type, data) 
                VALUES
                    (:id, :groupId, :funcId, :topicId, :hookId, :flowId, :type, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("groupId", obj.groupId)
            set("funcId", obj.funcId)
            if (obj is EventTrigger) {
                set("topicId", obj.topicId)
            } else {
                set("topicId", 0)
            }

            if (obj is HookTrigger) {
                set("hookId", obj.hookId)
            } else {
                set("hookId", 0)
            }
            set("flowId", obj.flowId)
            set("type", obj.type.value)
            set("data", json.serializeAndCompress(obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 group_id       INTEGER NOT NULL,
                 func_id        INTEGER NOT NULL,
                 type           INTEGER NOT NULL,
                 topic_id       INTEGER NOT NULL,
                 hook_id        INTEGER NOT NULL,
                 flow_id        INTEGER NOT NULL,
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
            "AND type IN (${types.joinToString(",") { "${it.value}" }})"
        }
    }

    private fun TriggerQuery.ids(): String {
        return if (triggerIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${triggerIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun TriggerQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun TriggerQuery.funcIds(): String {
        return if (funcIds.isEmpty()) {
            ""
        } else {
            "AND func_id IN (${funcIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun TriggerQuery.topicIds(): String {
        return if (topicIds.isEmpty()) {
            ""
        } else {
            "AND topic_id IN (${topicIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun TriggerQuery.hookIds(): String {
        return if (hookIds.isEmpty()) {
            ""
        } else {
            "AND hook_id IN (${hookIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun TriggerQuery.flowIds(): String {
        return if (flowIds.isEmpty()) {
            ""
        } else {
            "AND flow_id IN (${flowIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
