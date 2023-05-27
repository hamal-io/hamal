package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.BrokerTopicsRepository
import io.hamal.backend.repository.api.log.Topic
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.nio.file.Path

data class BrokerTopics(
    val brokerId: Broker.Id,
    val path: Path
)

class DefaultBrokerTopicsRepository(
    internal val brokerTopics: BrokerTopics,
) : BaseRepository(object : Config {
    override val path: Path get() = brokerTopics.path
    override val filename: String get() = "topics.db"
    override val shard: Shard get() = Shard(0)

}), BrokerTopicsRepository {

    private val topicMapping = KeyedOnce.default<TopicName, Topic>()
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

    override fun resolveTopic(name: TopicName): Topic {
        return topicMapping.invoke(name) {
            connection.tx {
                val id = findTopicId(name) ?: createTopic(name)
                Topic(
                    id = id,
                    brokerId = brokerTopics.brokerId,
                    name = name,
                    path = brokerTopics.path,
                    shard = Shard(0)
                )
            }!!
        }
    }

    override fun find(topicId: TopicId): Topic? = findById(topicId)


    override fun clear() {
        connection.tx {
            execute("DELETE FROM topics")
            execute("DELETE FROM sqlite_sequence")
        }
    }

    override fun close() {
        connection.close()
    }
}

fun DefaultBrokerTopicsRepository.count() = connection.executeQueryOne("SELECT COUNT(*) as count from topics") {
    map {
        it.getLong("count").toULong()
    }
} ?: 0UL

private fun DefaultBrokerTopicsRepository.findById(topicId: TopicId): Topic? {
    return connection.executeQueryOne("SELECT id,name FROM topics WHERE id = :id") {
        with {
            set("id", topicId.value)
        }
        map { rs ->
            Topic(
                id = TopicId(rs.getInt("id")),
                brokerId = brokerTopics.brokerId,
                name = TopicName(rs.getString("name")),
                path = brokerTopics.path,
                shard = Shard(0)
            )
        }
    }
}

private fun DefaultBrokerTopicsRepository.findTopicId(name: TopicName): TopicId? {
    return connection.executeQueryOne("SELECT id FROM topics WHERE name = :name") {
        with {
            set("name", name.value)
        }
        map {
            TopicId(it.getInt("id"))
        }
    }
}

private fun DefaultBrokerTopicsRepository.createTopic(name: TopicName): TopicId {
    return connection.execute<TopicId>("INSERT INTO topics(name, instant) VALUES (:name, :now) RETURNING id") {
        with {
            set("name", name.value)
            set("now", TimeUtils.now())
        }
        map { TopicId(it.getInt("id")) }
    }!!
}