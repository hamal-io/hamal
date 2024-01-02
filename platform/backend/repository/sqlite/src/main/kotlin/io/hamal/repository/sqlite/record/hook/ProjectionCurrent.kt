package io.hamal.repository.sqlite.record.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.record.hook.HookRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
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
//                protobuf.decodeFromByteArray(Hook.serializer(), rs.getBytes("data"))
                TODO()
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
                ${query.groupIds()}
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
//                protobuf.decodeFromByteArray(Hook.serializer(), rs.getBytes("data"))
                TODO()
            }
        }
    }

    fun count(connection: Connection, query: HookQuery): ULong {
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
                ${query.flowIds()}
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

    override fun upsert(tx: RecordTransactionSqlite<HookId, HookRecord, Hook>, obj: Hook) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id, group_id, flow_id, data) 
                VALUES
                    (:id, :groupId, :flowId, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("groupId", obj.groupId)
            set("flowId", obj.flowId)
//            set("data", protobuf.encodeToByteArray(Hook.serializer(), obj))
            TODO()
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 group_id       INTEGER NOT NULL,
                 flow_id   INTEGER NOT NULL,
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

    private fun HookQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun HookQuery.flowIds(): String {
        return if (flowIds.isEmpty()) {
            ""
        } else {
            "AND flow_id IN (${flowIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
