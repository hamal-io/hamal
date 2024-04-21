package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Trigger
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueHook : ProjectionSqlite<TriggerId, TriggerRecord, Trigger> {
    override fun upsert(tx: RecordTransactionSqlite<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        obj as Trigger.Hook
        try {
            tx.execute(
                """
                INSERT OR FAIL INTO unique_hook 
                    (id, func_id, hook_id, namespace_id)  
                VALUES
                    (:id, :funcId, :hookId, :namespaceId);
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("funcId", obj.funcId)
                set("hookId", obj.hookId)
                set("namespaceId", obj.namespaceId)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("UNIQUE constraint failed: unique_hook.func_id, unique_hook.hook_id")) {
                throw IllegalArgumentException("Trigger already exists")
            }
            throw e
        }

    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_hook (
                 id             INTEGER NOT NULL,
                 func_id        INTEGER NOT NULL,
                 hook_id        INTEGER NOT NULL,
                 namespace_id   INTEGER NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (func_id, hook_id, namespace_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_hook""")
    }
}