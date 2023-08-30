package io.hamal.repository.sqlite.log

import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.Topic
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

class SqliteBrokerTopicsRepository(
    internal val brokerTopics: SqliteBrokerTopics,
) : BaseSqliteRepository(object : Config {
    override val path: Path get() = brokerTopics.path
    override val filename: String get() = "topics.db"
}), BrokerTopicsRepository {

    private val topicMapping = ConcurrentHashMap<TopicName, Topic>()
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

    override fun create(cmdId: CmdId, toCreate: BrokerTopicsRepository.TopicToCreate): Topic {
        try {
            return connection.execute<Topic>("INSERT INTO topics(id, name, instant) VALUES (:id, :name, :now) RETURNING id,name") {
                query {
                    set("id", toCreate.id)
                    set("name", toCreate.name)
                    set("now", TimeUtils.now())
                }
                map { rs ->
                    Topic(
                        id = rs.getDomainId("id", ::TopicId),
                        name = TopicName(rs.getString("name")),
                    )
                }
            }!!
        } catch (t: Throwable) {
            if (t.message!!.contains("(UNIQUE constraint failed: topics.name)")) {
                throw IllegalArgumentException("Topic already exists")
            }
            throw t
        }
    }

    override fun find(name: TopicName): Topic? =
        topicMapping[name] ?: connection.executeQueryOne("SELECT id, name FROM topics WHERE name = :name") {
            query {
                set("name", name.value)
            }
            map { rs ->
                Topic(
                    id = rs.getDomainId("id", ::TopicId), name = TopicName(rs.getString("name"))
                )
            }
        }?.also { topicMapping[it.name] = it }

    override fun find(id: TopicId): Topic? = topicMapping.values.find { it.id == id }
        ?: connection.executeQueryOne("SELECT id,name FROM topics WHERE id = :id") {
            query {
                set("id", id)
            }
            map { rs ->
                Topic(
                    id = rs.getDomainId("id", ::TopicId), name = TopicName(rs.getString("name"))
                )
            }
        }?.also { topicMapping[it.name] = it }

    override fun list(block: TopicQuery.() -> Unit): List<Topic> {
        val query = TopicQuery().also(block)
        return connection.executeQuery<Topic>(
            """
                SELECT
                    id, name 
                FROM 
                    topics
                WHERE
                    id < :afterId
                    ${query.names()}
                ORDER BY id DESC
                LIMIT :limit
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                Topic(
                    id = rs.getDomainId("id", ::TopicId), name = TopicName(rs.getString("name"))
                )
            }
        }
    }

    override fun count(block: TopicQuery.() -> Unit): ULong {
        val query = TopicQuery().also(block)
        return connection.executeQueryOne(
            """
            SELECT 
                COUNT(*) as count 
            FROM 
                topics
            WHERE
                id < :afterId
                ${query.names()}
            ORDER BY id DESC
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
            }
            map {
                it.getLong("count").toULong()
            }
        } ?: 0UL
    }

    override fun clear() {
        connection.tx {
            execute("DELETE FROM topics")
        }
    }

    override fun close() {
        connection.close()
    }


    private fun TopicQuery.names(): String {
        return if (names.isEmpty()) {
            ""
        } else {
            "AND name IN (${names.joinToString(",") { "'${it.value}'" }})"
        }
    }

}
