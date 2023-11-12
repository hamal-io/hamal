package io.hamal.repository.sqlite.record.group

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import io.hamal.repository.sqlite.record.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import org.sqlite.SQLiteException


@OptIn(ExperimentalSerializationApi::class)
internal object ProjectionCurrent : SqliteProjection<GroupId, GroupRecord, Group> {
    override fun upsert(tx: SqliteRecordTransaction<GroupId, GroupRecord, Group>, obj: Group) {
        try {
            tx.execute(
                """
                INSERT OR REPLACE INTO current
                    (id, data) 
                VALUES
                    (:id, :data)
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("data", protobuf.encodeToByteArray(Group.serializer(), obj))
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("UNIQUE constraint failed: current.name)")) {
                throw IllegalArgumentException("${obj.name} already exists")
            }
            throw e
        }
    }

    private fun find(tx: SqliteRecordTransaction<GroupId, GroupRecord, Group>, groupName: GroupName): Group? {
        return tx.executeQueryOne(
            """
                SELECT 
                    name
                FROM
                    current
                WHERE
                    name = :name
            """.trimIndent()
        ) {
            query {
                set("name", groupName.value)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Group.serializer(), rs.getBytes("data"))
            }
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

    fun find(connection: Connection, groupId: GroupId): Group? {
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
                set("id", groupId)
            }
            map { rs ->
                protobuf.decodeFromByteArray(Group.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun list(connection: Connection, query: GroupQuery): List<Group> {
        return connection.executeQuery<Group>(
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
                protobuf.decodeFromByteArray(Group.serializer(), rs.getBytes("data"))
            }
        }
    }

    fun count(connection: Connection, query: GroupQuery): ULong {
        return connection.executeQueryOne(
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
                it.getLong("count").toULong()
            }
        } ?: 0UL
    }

    private fun GroupQuery.ids(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }


}
