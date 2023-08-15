package io.hamal.backend.repository.sqlite.record.trigger

import io.hamal.backend.repository.api.Trigger
import io.hamal.backend.repository.record.trigger.TriggerRecord
import io.hamal.backend.repository.sqlite.record.Projection
import io.hamal.backend.repository.sqlite.record.RecordTransaction
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sqlite.Connection

internal object ProjectionUniqueName : Projection<TriggerId, TriggerRecord, Trigger> {

    override fun upsert(tx: RecordTransaction<TriggerId, TriggerRecord, Trigger>, obj: Trigger) {
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

    override fun clear(connection: Connection) {
        connection.execute("""DELETE FROM unique_names""")
    }

    override fun invalidate() {}
}
