package io.hamal.repository.sqlite.record.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.api.Topic
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.sqlite.record.ProjectionSqlite
import io.hamal.repository.sqlite.record.RecordTransactionSqlite
import org.sqlite.SQLiteException

internal object ProjectionUniqueName : ProjectionSqlite<TopicId, TopicRecord, Topic> {

    override fun upsert(tx: RecordTransactionSqlite<TopicId, TopicRecord, Topic>, obj: Topic) {
        try {
            tx.execute(
                """
                INSERT INTO unique_name (id,group_id, name)  VALUES(:id,:groupId, :name)
                    ON CONFLICT(id) DO UPDATE
                        SET name=:name, group_id=:groupId;
            """.trimIndent()
            ) {
                set("id", obj.id)
                set("groupId", obj.groupId)
                set("name", obj.name)
            }
        } catch (e: SQLiteException) {
            if (e.message!!.contains("(UNIQUE constraint failed: unique_name.group_id, unique_name.name)")) {
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
