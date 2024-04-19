package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Trigger
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueEndpoint : ProjectionSqlite<TriggerId, TriggerRecord, Trigger> {
    override fun upsert(tx: RecordTransactionSqlite<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        obj as Trigger.Endpoint
        try {
            tx.execute(
                """
                INSERT OR FAIL INTO unique_endpoint 
                    (id, func_id, endpoint_id)  
                VALUES
                    (:id, :funcId, :endpointId);
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("funcId", obj.funcId)
                set("endpointId", obj.endpointId)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("UNIQUE constraint failed: unique_endpoint.func_id, unique_endpoint.endpoint_id")) {
                throw IllegalArgumentException("Trigger already exists")
            }
            throw e
        }

    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_endpoint (
                 id             INTEGER NOT NULL,
                 func_id        INTEGER NOT NULL,
                 endpoint_id    INTEGER NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (func_id, endpoint_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_endpoint""")
    }
}