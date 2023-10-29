package io.hamal.repository.sqlite.record.blueprint

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
object ProjectionCurrent : SqliteProjection<BlueprintId, BlueprintRecord, Blueprint> {


    override fun upsert(tx: SqliteRecordTransaction<BlueprintId, BlueprintRecord, Blueprint>, obj: Blueprint) {
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
            set("data", protobuf.encodeToByteArray(Blueprint.serializer(), obj))
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

    fun find(connection: Connection, blueprintId: BlueprintId): Blueprint? {
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
                set("id", blueprintId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Blueprint.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: BlueprintQuery): List<Blueprint> {
        return connection.executeQuery<Blueprint>(
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
                protobuf.decodeFromByteArray(Blueprint.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: BlueprintQuery): ULong {
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

    private fun BlueprintQuery.ids(): String {
        return if (blueprintIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${blueprintIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun BlueprintQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}