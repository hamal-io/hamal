package io.hamal.repository.sqlite.record.flow

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.record.flow.FlowRecord
import io.hamal.repository.record.json
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

internal object ProjectionCurrent : ProjectionSqlite<FlowId, FlowRecord, Flow> {

    fun find(connection: Connection, flowId: FlowId): Flow? {
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
                set("id", flowId)
            }
            map { rs ->
                json.decompressAndDeserialize(Flow::class, rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: FlowQuery): List<Flow> {
        return connection.executeQuery<Flow>(
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
                json.decompressAndDeserialize(Flow::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: FlowQuery): Count {
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

    override fun upsert(tx: RecordTransactionSqlite<FlowId, FlowRecord, Flow>, obj: Flow) {
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
            set("data", json.serializeAndCompress(obj))
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

    private fun FlowQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun FlowQuery.ids(): String {
        return if (flowIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${flowIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
