package io.hamal.repository.sqlite.record.endpoint

import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.record.endpoint.EndpointRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : ProjectionSqlite<EndpointId, EndpointRecord, Endpoint> {

    fun find(connection: Connection, endpointId: EndpointId): Endpoint? {
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
                set("id", endpointId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Endpoint.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: EndpointQuery): List<Endpoint> {
        return connection.executeQuery<Endpoint>(
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
                protobuf.decodeFromByteArray(Endpoint.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: EndpointQuery): ULong {
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

    override fun upsert(tx: RecordTransactionSqlite<EndpointId, EndpointRecord, Endpoint>, obj: Endpoint) {
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
            set("data", protobuf.encodeToByteArray(Endpoint.serializer(), obj))
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

    private fun EndpointQuery.ids(): String {
        return if (endpointIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${endpointIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun EndpointQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun EndpointQuery.flowIds(): String {
        return if (flowIds.isEmpty()) {
            ""
        } else {
            "AND flow_id IN (${flowIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
