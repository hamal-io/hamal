package io.hamal.repository.sqlite.record.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository.CodeQuery
import io.hamal.repository.record.code.CodeRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : ProjectionSqlite<CodeId, CodeRecord, Code> {

    fun find(connection: Connection, codeId: CodeId): Code? {
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
                set("id", codeId)
            }
            map { rs ->
//                protobuf.decodeFromByteArray(Code.serializer(), rs.getBytes("data"))
                TODO()
            }
        }
    }


    fun list(connection: Connection, query: CodeQuery): List<Code> {
        return connection.executeQuery<Code>(
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
//                protobuf.decodeFromByteArray(Code.serializer(), rs.getBytes("data"))
                TODO()
            }
        }
    }

    fun count(connection: Connection, query: CodeQuery): ULong {
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

    override fun upsert(tx: RecordTransactionSqlite<CodeId, CodeRecord, Code>, obj: Code) {
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
//            set("data", protobuf.encodeToByteArray(Code.serializer(), obj))
            TODO()
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

    private fun CodeQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun CodeQuery.ids(): String {
        return if (codeIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${codeIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}