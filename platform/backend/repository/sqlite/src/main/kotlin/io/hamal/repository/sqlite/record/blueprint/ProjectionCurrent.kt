package io.hamal.repository.sqlite.record.blueprint

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import io.hamal.repository.record.blueprint.BlueprintRecord
import io.hamal.repository.record.json
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite

object ProjectionCurrent : ProjectionSqlite<BlueprintId, BlueprintRecord, Blueprint> {


    override fun upsert(tx: RecordTransactionSqlite<BlueprintId, BlueprintRecord, Blueprint>, obj: Blueprint) {
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
                json.decompressAndDeserialize(Blueprint::class, rs.getBytes("data"))
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
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                json.decompressAndDeserialize(Blueprint::class, rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: BlueprintQuery): Count {
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

    private fun BlueprintQuery.ids(): String {
        return if (blueprintIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${blueprintIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}