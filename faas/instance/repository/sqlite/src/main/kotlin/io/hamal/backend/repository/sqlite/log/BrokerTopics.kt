package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogBrokerTopicsRepository
import io.hamal.backend.repository.api.log.LogTopic
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

data class SqliteBrokerTopics(
    val path: Path
)

class SqliteLogBrokerTopicsRepository(
    internal val brokerTopics: SqliteBrokerTopics,
) : BaseSqliteRepository(object : Config {
    override val path: Path get() = brokerTopics.path
    override val filename: String get() = "topics.db"

}), LogBrokerTopicsRepository {

    private val topicMapping = ConcurrentHashMap<TopicName, LogTopic>()
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

    override fun create(cmdId: CmdId, toCreate: LogBrokerTopicsRepository.TopicToCreate): LogTopic {
        return connection.execute<LogTopic>("INSERT INTO topics(id, name, instant) VALUES (:id, :name, :now) RETURNING id,name") {
            query {
                set("id", toCreate.id)
                set("name", toCreate.name)
                set("now", TimeUtils.now())
            }
            map { rs ->
                LogTopic(
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name")),
                )
            }
        }!!
    }

    override fun find(name: TopicName): LogTopic? =
        topicMapping[name] ?: connection.executeQueryOne("SELECT id, name FROM topics WHERE name = :name") {
            query {
                set("name", name.value)
            }
            map { rs ->
                LogTopic(
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name"))
                )
            }
        }?.also { topicMapping[it.name] = it }

    override fun find(id: TopicId): LogTopic? =
        topicMapping.values.find { it.id == id }
            ?: connection.executeQueryOne("SELECT id,name FROM topics WHERE id = :id") {
                query {
                    set("id", id)
                }
                map { rs ->
                    LogTopic(
                        id = rs.getDomainId("id", ::TopicId),
                        name = TopicName(rs.getString("name"))
                    )
                }
            }?.also { topicMapping[it.name] = it }

    override fun list(): List<LogTopic> {
        return connection.executeQuery<LogTopic>("SELECT id,name FROM topics") {
            map { rs ->
                LogTopic(
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name"))
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
