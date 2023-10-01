package io.hamal.repository.sqlite.record.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.record.func.FuncRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : SqliteProjection<FuncId, FuncRecord, Func> {

    fun find(connection: Connection, funcId: FuncId): Func? {
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
                set("id", funcId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Func.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: FuncQuery): List<Func> {
        return connection.executeQuery<Func>(
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
                protobuf.decodeFromByteArray(Func.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: FuncQuery): ULong {
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

    override fun upsert(tx: SqliteRecordTransaction<FuncId, FuncRecord, Func>, obj: Func) {
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
            set("data", protobuf.encodeToByteArray(Func.serializer(), obj))
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

    private fun FuncQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun FuncQuery.ids(): String {
        return if (funcIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${funcIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
