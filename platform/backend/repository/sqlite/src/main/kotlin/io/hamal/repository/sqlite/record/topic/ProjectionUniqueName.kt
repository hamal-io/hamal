package io.hamal.repository.sqlite.record.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sqlite.Connection
import io.hamal.repository.api.Topic
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : ProjectionSqlite.UniqueImpl<TopicId, TopicRecord, Topic>("unique_name") {

    override fun upsert(tx: RecordTransactionSqlite<TopicId, TopicRecord, Topic>, obj: Topic) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id,namespace_id, name)  VALUES(:id,:namespaceId, :name)
                    ON CONFLICT(id) DO UPDATE
                        SET name=:name, namespace_id=:namespaceId;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("namespaceId", obj.namespaceId)
                set("name", obj.name)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.namespace_id, unique_name.name)")) {
                throw IllegalArgumentException("Topic already exists")
            }
            throw e
        }
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS unique_name (
                 id             INTEGER NOT NULL,
                 namespace_id       INTEGER NOT NULL,
                 name           VARCHAR(255) NOT NULL,
                 PRIMARY KEY    (id),
                 UNIQUE (namespace_id, name)
            );
        """.trimIndent()
        )
    }

}
