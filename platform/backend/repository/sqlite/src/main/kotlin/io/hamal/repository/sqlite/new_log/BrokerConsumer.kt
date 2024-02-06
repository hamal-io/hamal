package io.hamal.repository.sqlite.new_log

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.log.LogConsumerId
import io.hamal.repository.api.log.LogEventId
import java.nio.file.Path


internal class LogBrokerConsumersSqliteRepository(
    val path: Path
) : SqliteBaseRepository(
    object : Config {
        override val path: Path get() = path
        override val filename: String get() = "log-broker-consumers.db"
    }
) {

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.tx {
            execute(
                """
                CREATE TABLE IF NOT EXISTS consumers (
                   group_id TEXT NOT NULL ,
                   topic_id INTEGER NOT NULL ,
                   next_event_id INTEGER NOT NULL ,
                   PRIMARY KEY (topic_id,group_id)
               )
            """.trimIndent()
            )
        }
    }

    fun nextEventId(consumerId: LogConsumerId, topicId: LogTopicId): LogEventId {
        return connection.executeQueryOne(
            """SELECT next_event_id FROM consumers WHERE group_id = :groupId and topic_id = :topicId"""
        ) {
            query {
                set("groupId", consumerId.value)
                set("topicId", topicId.value)
            }
            map {
                LogEventId(it.getInt("next_event_id"))
            }
        } ?: LogEventId(0)
    }

    fun commit(consumerId: LogConsumerId, topicId: LogTopicId, eventId: LogEventId) {
        connection.execute(
            """
            INSERT INTO consumers(group_id, topic_id, next_event_id)
                  VALUES(:groupId, :topicId, :nextEventId)
                     ON CONFLICT(group_id, topic_id) DO UPDATE SET
                        next_event_id=excluded.next_event_id
                        WHERE
                            excluded.group_id  == consumers.group_id AND
                            excluded.topic_id  == consumers.topic_id;
                    """.trimIndent(),
        ) {
            set("groupId", consumerId.value)
            set("topicId", topicId.value)
            set("nextEventId", eventId.value.toInt() + 1)
        }
    }

    override fun clear() {
        connection.execute("DELETE FROM consumers")
    }


    override fun close() {
        connection.close()
    }

    fun count() = connection.executeQueryOne("SELECT COUNT(*) as count from consumers") {
        map {
            Count(it.getLong("count"))
        }
    } ?: Count.None
}