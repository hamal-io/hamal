package io.hamal.repository.sqlite.record.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.record.exec.ExecRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : SqliteProjection<ExecId, ExecRecord, Exec> {
    fun find(connection: Connection, execId: ExecId): Exec? {
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
                set("id", execId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Exec.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: ExecQuery): List<Exec> {
        return connection.executeQuery<Exec>(
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
                protobuf.decodeFromByteArray(Exec.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: ExecQuery): ULong {
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


    override fun upsert(
        tx: SqliteRecordTransaction<ExecId, ExecRecord, Exec>,
        obj: Exec
    ) {
        tx.execute(
            """
                INSERT OR REPLACE INTO current
                    (id,status,group_id,data) 
                VALUES
                    (:id,:status,:groupId,:data)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("status", obj.status.value)
            set("groupId", obj.groupId)
            set("data", protobuf.encodeToByteArray(Exec.serializer(), obj))
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 status         INTEGER NOT NULL,
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

    private fun ExecQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun ExecQuery.ids(): String {
        return if (execIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${execIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
