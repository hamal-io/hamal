package io.hamal.repository.sqlite.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate
import io.hamal.repository.api.log.Topic
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

data class SqliteBrokerTopics(
    val path: Path
)

class SqliteBrokerTopicsRepository(
    internal val brokerTopics: SqliteBrokerTopics,
) : SqliteBaseRepository(object : Config {
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
            id              INTEGER PRIMARY KEY,
            name            TEXT NOT NULL ,
            group_id        INTEGER NOT NULL,
            flow_id    INTEGER NOT NULL,
            instant         DATETIME NOT NULL,
            UNIQUE(flow_id, name)
        );
        """
            )
        }
    }

    override fun create(cmdId: CmdId, toCreate: TopicToCreate): Topic {
        try {
            return connection.execute<Topic>("INSERT INTO topics(id, name,flow_id, group_id, instant) VALUES (:id, :name,:flowId, :groupId, :now) RETURNING id, name, flow_id, group_id") {
                query {
                    set("id", toCreate.id)
                    set("name", toCreate.name)
                    set("flowId", toCreate.flowId)
                    set("groupId", toCreate.groupId)
                    set("now", TimeUtils.now())
                }
                map { rs ->
                    Topic(
                        id = rs.getDomainId("id", ::TopicId),
                        name = TopicName(rs.getString("name")),
                        flowId = rs.getDomainId("flow_id", ::FlowId),
                        groupId = rs.getDomainId("group_id", ::GroupId)
                    )
                }
            }!!
        } catch (t: Throwable) {
            if (t.message!!.contains("(UNIQUE constraint failed: topics.flow_id, topics.name)")) {
                throw IllegalArgumentException("Topic already exists")
            }
            throw t
        }
    }

    override fun find(flowId: FlowId, name: TopicName): Topic? =
        topicMapping[name]
            ?: connection.executeQueryOne("SELECT id, name, flow_id, group_id FROM topics WHERE name = :name AND flow_id = :flowId") {
                query {
                    set("name", name.value)
                    set("flowId", flowId.value)
                }
                map { rs ->
                    Topic(
                        id = rs.getDomainId("id", ::TopicId),
                        name = TopicName(rs.getString("name")),
                        flowId = rs.getDomainId("flow_id", ::FlowId),
                        groupId = rs.getDomainId("group_id", ::GroupId)
                    )
                }
            }?.also { topicMapping[it.name] = it }

    override fun find(id: TopicId): Topic? = topicMapping.values.find { it.id == id }
        ?: connection.executeQueryOne("SELECT id, name, flow_id, group_id FROM topics WHERE id = :id") {
            query {
                set("id", id)
            }
            map { rs ->
                Topic(
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name")),
                    flowId = rs.getDomainId("flow_id", ::FlowId),
                    groupId = rs.getDomainId("group_id", ::GroupId)
                )
            }
        }?.also { topicMapping[it.name] = it }

    override fun list(query: TopicQuery): List<Topic> {
        return connection.executeQuery<Topic>(
            """
                SELECT
                    id, name, flow_id, group_id
                FROM 
                    topics
                WHERE
                    id < :afterId
                    ${query.names()}
                    ${query.groupIds()}
                    ${query.flowIds()}
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
                    id = rs.getDomainId("id", ::TopicId),
                    name = TopicName(rs.getString("name")),
                    flowId = rs.getDomainId("flow_id", ::FlowId),
                    groupId = rs.getDomainId("group_id", ::GroupId)
                )
            }
        }
    }

    override fun count(query: TopicQuery): ULong {
        return connection.executeQueryOne(
            """
            SELECT 
                COUNT(*) as count 
            FROM 
                topics
            WHERE
                id < :afterId
                ${query.names()}
                ${query.groupIds()}
                ${query.flowIds()}
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

    private fun TopicQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }

    private fun TopicQuery.flowIds(): String {
        return if (flowIds.isEmpty()) {
            ""
        } else {
            "AND flow_id IN (${flowIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
