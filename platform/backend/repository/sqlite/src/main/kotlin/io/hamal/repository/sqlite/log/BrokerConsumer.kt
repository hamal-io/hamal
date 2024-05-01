package io.hamal.repository.sqlite.log

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.log.LogBrokerRepository.LogConsumerQuery
import io.hamal.repository.api.log.LogConsumerId
import io.hamal.repository.api.log.LogEventId
import io.hamal.repository.api.log.LogEventId.Companion.LogEventId
import java.nio.file.Path


internal class LogBrokerConsumerSqliteRepository(
    val path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "log-broker-consumer.db"
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
                   workspace_id TEXT NOT NULL ,
                   topic_id INTEGER NOT NULL ,
                   next_event_id INTEGER NOT NULL ,
                   PRIMARY KEY (topic_id,workspace_id)
               )
            """.trimIndent()
            )
        }
    }

    fun nextEventId(consumerId: LogConsumerId, topicId: LogTopicId): LogEventId {
        return connection.executeQueryOne(
            """SELECT next_event_id FROM consumers WHERE workspace_id = :workspaceId and topic_id = :topicId"""
        ) {
            query {
                set("workspaceId", consumerId)
                set("topicId", topicId)
            }
            map {
                it.getId("next_event_id", ::LogEventId)
            }
        } ?: LogEventId(0)
    }

    fun commit(consumerId: LogConsumerId, topicId: LogTopicId, eventId: LogEventId) {
        connection.execute(
            """
            INSERT INTO consumers(workspace_id, topic_id, next_event_id)
                  VALUES(:workspaceId, :topicId, :nextEventId)
                     ON CONFLICT(workspace_id, topic_id) DO UPDATE SET
                        next_event_id=excluded.next_event_id
                        WHERE
                            excluded.workspace_id  == consumers.workspace_id AND
                            excluded.topic_id  == consumers.topic_id;
                    """.trimIndent(),
        ) {
            set("workspaceId", consumerId)
            set("topicId", topicId)
            set("nextEventId", eventId.longValue + 1)
        }
    }

    override fun clear() {
        connection.execute("DELETE FROM consumers")
    }


    override fun close() {
        connection.close()
    }

    fun count(query: LogConsumerQuery) = connection.executeQueryOne("SELECT COUNT(*) as count from consumers") {
        map {
            Count(it.getLong("count"))
        }
    } ?: Count.None
}