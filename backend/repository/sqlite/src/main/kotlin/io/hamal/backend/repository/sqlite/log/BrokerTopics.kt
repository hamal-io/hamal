package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.BrokerTopics
import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.backend.repository.api.log.LogBrokerTopicsRepository
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.nio.file.Path

data class SqliteBrokerTopics(
    override val logBrokerId: LogBroker.Id,
    val path: Path
) : BrokerTopics

class SqliteLogBrokerTopicsRepository(
    internal val brokerTopics: SqliteBrokerTopics,
) : BaseRepository(object : Config {
    override val path: Path get() = brokerTopics.path
    override val filename: String get() = "topics.db"

}), LogBrokerTopicsRepository<SqliteLogTopic> {

    private val topicMapping = KeyedOnce.default<TopicName, SqliteLogTopic>()
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
         CREATE TABLE IF NOT EXISTS topics (
            id INTEGER PRIMARY KEY AUTOINCREMENT ,
            name TEXT NOT NULL ,
            instant DATETIME NOT NULL,
            UNIQUE(name)
        );
        """
            )
        }
    }

    override fun resolveTopic(name: TopicName): SqliteLogTopic {
        return topicMapping.invoke(name) {
            connection.tx {
                val id = findTopicId(name) ?: createTopic(name)
                SqliteLogTopic(
                    id = id,
                    logBrokerId = brokerTopics.logBrokerId,
                    name = name,
                    path = brokerTopics.path
                )
            }
        }
    }

    override fun find(topicId: TopicId): SqliteLogTopic? = findById(topicId)

    override fun clear() {
        connection.tx {
            execute("DELETE FROM topics")
            execute("DELETE FROM sqlite_sequence")
        }
    }

    override fun close() {
        connection.close()
    }

    override fun count() = connection.executeQueryOne("SELECT COUNT(*) as count from topics") {
        map {
            it.getLong("count").toULong()
        }
    } ?: 0UL
}


private fun SqliteLogBrokerTopicsRepository.findById(topicId: TopicId): SqliteLogTopic? {
    return connection.executeQueryOne("SELECT id,name FROM topics WHERE id = :id") {
        query {
            set("id", topicId.value)
        }
        map { rs ->
            SqliteLogTopic(
                id = rs.getDomainId("id", ::TopicId),
                logBrokerId = brokerTopics.logBrokerId,
                name = TopicName(rs.getString("name")),
                path = brokerTopics.path
            )
        }
    }
}

private fun SqliteLogBrokerTopicsRepository.findTopicId(name: TopicName): TopicId? {
    return connection.executeQueryOne("SELECT id FROM topics WHERE name = :name") {
        query {
            set("name", name.value)
        }
        map {
            it.getDomainId("id", ::TopicId)
        }
    }
}

private fun SqliteLogBrokerTopicsRepository.createTopic(name: TopicName): TopicId {
    return connection.execute<TopicId>("INSERT INTO topics(name, instant) VALUES (:name, :now) RETURNING id") {
        query {
            set("name", name.value)
            set("now", TimeUtils.now())
        }
        map { it.getDomainId("id", ::TopicId) }
    }!!
}