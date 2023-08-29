package io.hamal.repository.sqlite.log

import io.hamal.repository.api.log.GroupId
import io.hamal.repository.api.log.LogBrokerConsumersRepository
import io.hamal.repository.api.log.LogChunkId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import java.nio.file.Path

data class SqliteBrokerConsumers(
    val path: Path
)

class SqliteLogBrokerConsumersRepository(
    internal val brokerConsumers: SqliteBrokerConsumers,
) : BaseSqliteRepository(
    object : Config {
        override val path: Path get() = brokerConsumers.path
        override val filename: String get() = "consumers.db"

    }
), LogBrokerConsumersRepository {

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
                   next_chunk_id INTEGER NOT NULL ,
                   PRIMARY KEY (topic_id,group_id)
               )
            """.trimIndent()
            )
        }
    }

    override fun nextChunkId(groupId: GroupId, topicId: TopicId): LogChunkId {
        return connection.executeQueryOne(
            """SELECT next_chunk_id FROM consumers WHERE group_id = :groupId and topic_id = :topicId"""
        ) {
            query {
                set("groupId", groupId.value)
                set("topicId", topicId.value)
            }
            map {
                LogChunkId(it.getSnowflakeId("next_chunk_id"))
            }
        } ?: LogChunkId(0)
    }

    override fun commit(groupId: GroupId, topicId: TopicId, chunkId: LogChunkId) {
        connection.execute(
            """
            INSERT INTO consumers(group_id, topic_id, next_chunk_id)
                  VALUES(:groupId, :topicId, :nextChunkId)
                     ON CONFLICT(group_id, topic_id) DO UPDATE SET
                        next_chunk_id=excluded.next_chunk_id
                        WHERE
                            excluded.group_id  == consumers.group_id AND
                            excluded.topic_id  == consumers.topic_id;
                    """.trimIndent(),
        ) {
            set("groupId", groupId.value)
            set("topicId", topicId.value)
            set("nextChunkId", chunkId.value.value + 1)
        }
    }

    override fun clear() {
        connection.execute("DELETE FROM consumers")
    }


    override fun close() {
        connection.close()
    }

    override fun count() = connection.executeQueryOne("SELECT COUNT(*) as count from consumers") {
        map {
            it.getLong("count").toULong()
        }
    } ?: 0UL
}