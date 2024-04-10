package com.nyanbot.repository.impl.flow

import com.nyanbot.json
import com.nyanbot.repository.Flow
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowQueryRepository.FlowQuery
import com.nyanbot.repository.impl.ProjectionSqlite
import com.nyanbot.repository.impl.RecordTransactionSqlite
import io.hamal.lib.common.domain.Count
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction


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
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("data", json.serializeAndCompress(obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    private fun FlowQuery.ids(): String {
        return if (flowIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${flowIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
