package io.hamal.repository.sqlite.record.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Hook
import io.hamal.repository.record.hook.HookRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : ProjectionSqlite<HookId, HookRecord, Hook> {

    override fun upsert(tx: RecordTransactionSqlite<HookId, HookRecord, Hook>, obj: Hook) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id, name, flow_id)  VALUES(:id, :name, :flowId)
                    ON CONFLICT(id) DO UPDATE 
                        SET name=:name, flow_id=:flowId;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("name", obj.name)
                set("flowId", obj.flowId)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.name, unique_name.flow_id)")) {
                throw IllegalArgumentException("${obj.name} already exists in namespace ${obj.flowId}")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_name (
                 id             INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 flow_id   INTEGER NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (name, flow_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_name""")
    }
    
}
