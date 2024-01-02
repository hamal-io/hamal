package io.hamal.repository.sqlite.record.extension

import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Extension
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import io.hamal.repository.record.extension.ExtensionRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionCurrent : ProjectionSqlite<ExtensionId, ExtensionRecord, Extension> {
    override fun upsert(tx: RecordTransactionSqlite<ExtensionId, ExtensionRecord, Extension>, obj: Extension) {
        try {
            tx.execute(
                """
                INSERT INTO current
                    (id, group_id, name, data) 
                VALUES
                    (:id, :groupId, :name, :data)
                ON CONFLICT(id) DO UPDATE 
                        SET name= :name, data= :data;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("groupId", obj.groupId)
                set("name", obj.name)
//                set("data", protobuf.encodeToByteArray(Extension.serializer(), obj))
                TODO()
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: current.group_id, current.name)")) {
                throw IllegalArgumentException("${obj.name} already exists in group ${obj.groupId}")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS current (
                 id             INTEGER NOT NULL,
                 group_id       INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (group_id, name)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM current""")
    }

    fun find(connection: Connection, extId: ExtensionId): Extension? {
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
                set("id", extId)
            }
            map { rs ->
//                protobuf.decodeFromByteArray(Extension.serializer(), rs.getBytes("data"))
                TODO()
            }
        }
    }

    fun list(connection: Connection, query: ExtensionQuery): List<Extension> {
        return connection.executeQuery<Extension>(
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
//                protobuf.decodeFromByteArray(Extension.serializer(), rs.getBytes("data"))
                TODO()
            }
        }
    }

    fun count(connection: Connection, query: ExtensionQuery): ULong {
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

    private fun ExtensionQuery.ids(): String {
        return if (extIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${extIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun ExtensionQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}