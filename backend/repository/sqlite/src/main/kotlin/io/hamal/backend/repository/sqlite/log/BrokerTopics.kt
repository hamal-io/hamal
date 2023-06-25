package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogBrokerTopicsRepository
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

data class SqliteBrokerTopics(
    val path: Path
)

class SqliteLogBrokerTopicsRepository(
    internal val brokerTopics: SqliteBrokerTopics,
) : BaseRepository(object : Config {
    override val path: Path get() = brokerTopics.path
    override val filename: String get() = "topics.db"

}), LogBrokerTopicsRepository<SqliteLogTopic> {

    private val topicMapping = ConcurrentHashMap<TopicName, SqliteLogTopic>()
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
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL ,
            instant DATETIME NOT NULL,
            UNIQUE(name)
        );
        """
            )
        }
    }

    override fun create(cmdId: CmdId, toCreate: LogBrokerTopicsRepository.TopicToCreate): SqliteLogTopic {
        return connection.execute<SqliteLogTopic>("INSERT INTO topics(id, name, instant) VALUES (:id, :name, :now) RETURNING id,name") {
            query {
                set("id", toCreate.id)
                set("name", toCreate.name)
                set("now", TimeUtils.now())
            }
            map { rs ->
                SqliteLogTopic(
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name")),
                    path = brokerTopics.path
                )
            }
        }!!
    }

    override fun find(name: TopicName): SqliteLogTopic? =
        topicMapping[name] ?: connection.executeQueryOne("SELECT id, name FROM topics WHERE name = :name") {
            query {
                set("name", name.value)
            }
            map { rs ->
                SqliteLogTopic(
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name")),
                    path = brokerTopics.path
                )
            }
        }?.also { topicMapping[it.name] = it }

    override fun find(id: TopicId): SqliteLogTopic? =
        topicMapping.values.find { it.id == id }
            ?: connection.executeQueryOne("SELECT id,name FROM topics WHERE id = :id") {
                query {
                    set("id", id)
                }
                map { rs ->
                    SqliteLogTopic(
                        id = rs.getDomainId("id", ::TopicId),
                        name = TopicName(rs.getString("name")),
                        path = brokerTopics.path
                    )
                }
            }?.also { topicMapping[it.name] = it }

    override fun list(): List<SqliteLogTopic> {
        return connection.executeQuery<SqliteLogTopic>("SELECT id,name FROM topics") {
            query {
                set("some_value", true)
            }
            map { rs ->
                SqliteLogTopic(
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name")),
                    path = brokerTopics.path
                )
            }
        }
    }

    override fun clear() {
        connection.tx {
            execute("DELETE FROM topics")
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
