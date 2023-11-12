package io.hamal.repository.sqlite.record.group

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Group
import io.hamal.repository.record.group.GroupRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : SqliteProjection<GroupId, GroupRecord, Group> {

    fun find(connection: Connection, groupName: GroupName): GroupId? {
        return connection.executeQueryOne(
            """
            SELECT 
                id
             FROM
                unique_name
            WHERE
                name  = :name
        """.trimIndent()
        ) {
            query {
                set("name", groupName)
            }
            map { rs -> rs.getDomainId("id", ::GroupId) }
        }
    }

    override fun upsert(tx: SqliteRecordTransaction<GroupId, GroupRecord, Group>, obj: Group) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id, name)  VALUES(:id, :name)
                    ON CONFLICT(id) DO UPDATE 
                        SET name=:name;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("name", obj.name)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.name)")) {
                throw IllegalArgumentException("${obj.name} already exists")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_name (
                 id             INTEGER NOT NULL,
                 name           VARCHAR(255) UNIQUE NOT NULL,
                 PRIMARY KEY    (id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_name""")
    }
}
