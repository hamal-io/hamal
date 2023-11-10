package io.hamal.repository.sqlite.record.flow

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Flow
import io.hamal.repository.record.flow.FlowRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : SqliteProjection<FlowId, FlowRecord, Flow> {

    fun find(connection: Connection, flowName: FlowName): FlowId? {
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
                set("name", flowName)
            }
            map { rs -> rs.getDomainId("id", ::FlowId) }
        }
    }

    override fun upsert(tx: SqliteRecordTransaction<FlowId, FlowRecord, Flow>, obj: Flow) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id, group_id, name)  VALUES(:id, :groupId, :name)
                    ON CONFLICT(id) DO UPDATE 
                        SET name=:name;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("groupId", obj.groupId)
                set("name", obj.name)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.group_id, unique_name.name)")) {
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
                 group_id       INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (group_id, name)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_name""")
    }
    
}
