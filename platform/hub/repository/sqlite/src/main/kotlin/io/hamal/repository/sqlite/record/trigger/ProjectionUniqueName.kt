package io.hamal.repository.sqlite.record.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Trigger
import io.hamal.repository.record.trigger.TriggerRecord
import io.hamal.repository.sqlite.record.SqliteProjection
import io.hamal.repository.sqlite.record.SqliteRecordTransaction

internal object ProjectionUniqueName : SqliteProjection<TriggerId, TriggerRecord, Trigger> {

    override fun upsert(tx: SqliteRecordTransaction<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
        tx.execute(
            """
                INSERT INTO unique_names
                    (id, name, namespace_id) 
                VALUES
                    (:id, :name, :namespaceId)
            """.trimIndent()
        ) {
            set("id", obj.id)
            set("name", obj.name)
            set("namespaceId", obj.namespaceId)
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_names (
                 id             INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 namespace_id   INTEGER NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (name, namespace_id)
            );
        """.trimIndent()
        )
    }

    override fun clear(tx: Transaction) {
        tx.execute("""DELETE FROM unique_names""")
    }

    override fun invalidate() {}
}
