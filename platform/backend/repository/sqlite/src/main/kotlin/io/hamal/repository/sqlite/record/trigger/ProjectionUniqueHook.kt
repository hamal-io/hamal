package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.HookTrigger
import io.hamal.repository.api.HookTrigger.UniqueHookTrigger
import io.hamal.repository.api.Trigger
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction
import org.sqlite.SQLiteException

internal object ProjectionUniqueHook : SqliteProjection<TriggerId, TriggerRecord, Trigger> {
    override fun upsert(tx: SqliteRecordTransaction<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        obj as HookTrigger
        try {
            tx.execute(
                """
                INSERT OR FAIL INTO unique_hook 
                    (id, func_id, hook_id, hook_method)  
                VALUES
                    (:id, :funcId, :hookId, :hookMethod);
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("funcId", obj.funcId)
                set("hookId", obj.hookId)
                set("hookMethod", obj.hookMethod.value)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("UNIQUE constraint failed: unique_hook.func_id, unique_hook.hook_id, unique_hook.hook_method")) {
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
                 hook_method    INTEGER NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (func_id, hook_id, hook_method)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_hook""")
    }

    fun ensureHookUnique(connection: Connection, trigger: UniqueHookTrigger): Boolean {
        return connection.executeQueryOne(
            """
            SELECT * FROM unique_hook WHERE 
                func_id = :funcId AND 
                hook_id = :hookId AND 
                hook_method = :hookMethod;
            """.trimIndent()
        ) {
            query {
                set("funcId", trigger.funcId)
                set("hookId", trigger.hookId)
                set("hookMethod", trigger.hookMethod.value)
            }
            map {
                true
            }
        } ?: false
    }
}