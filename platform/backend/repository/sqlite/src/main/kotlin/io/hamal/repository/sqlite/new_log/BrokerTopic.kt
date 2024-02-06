package io.hamal.repository.sqlite.new_log

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

internal class LogBrokerTopicsSqliteRepository(
    internal val path: Path
) : SqliteBaseRepository(object : Config {
    override val path: Path get() = path
    override val filename: String get() = "log-broker-topics.db"
}) {

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

//    fun create(cmdId: CmdId, toCreate: TopicToCreate): Topic {
//        try {
//            return connection.execute<DepTopic>("INSERT INTO topics(id, name,flow_id, group_id, instant) VALUES (:id, :name,:flowId, :groupId, :now) RETURNING id, name, flow_id, group_id") {
//                query {
//                    set("id", toCreate.id)
//                    set("name", toCreate.name)
//                    set("flowId", toCreate.flowId)
//                    set("groupId", toCreate.groupId)
//                    set("now", TimeUtils.now())
//                }
//                map { rs ->
//                    DepTopic(
//                        id = rs.getId("id", ::TopicId),
//                        name = TopicName(rs.getString("name")),
//                        flowId = rs.getId("flow_id", ::FlowId),
//                        groupId = rs.getId("group_id", ::GroupId)
//                    )
//                }
//            }
//        } catch (t: Throwable) {
//            if (t.message!!.contains("(UNIQUE constraint failed: topics.flow_id, topics.name)")) {
//                throw IllegalArgumentException("Topic already exists")
//            }
//            throw t
//        }
//    }

    fun find(groupId: GroupId, name: TopicName): Topic = TODO()
//        topicMapping[name]
//            ?: connection.executeQueryOne("SELECT id, name,  group_id FROM topics WHERE name = :name AND group_id = :groupId") {
//                query {
//                    set("name", name.value)
//                    set("flowId", groupId.value)
//                }
//                map { rs ->
////                    Topic(
////                        id = rs.getId("id", ::TopicId),
////                        name = TopicName(rs.getString("name")),
////                        groupId = rs.getId("group_id", ::GroupId)
////                    )
//                    TODO()
//                }
//            }?.also { topicMapping[it.name] = it }

    fun find(id: TopicId): Topic = TODO()
//    fun find(id: TopicId): Topic? = topicMapping.values.find { it.id == id }
//        ?: connection.executeQueryOne("SELECT id, name, flow_id, group_id FROM topics WHERE id = :id") {
//            query {
//                set("id", id)
//            }
//            map { rs ->
////                DepTopic(
////                    id = rs.getId("id", ::TopicId),
////                    name = TopicName(rs.getString("name")),
////                    flowId = rs.getId("flow_id", ::FlowId),
////                    groupId = rs.getId("group_id", ::GroupId)
////                )
//                TODO()
//            }
//        }?.also { topicMapping[it.name] = it }

    fun list(query: TopicQueryRepository.TopicQuery): List<Topic> {
        return connection.executeQuery<Topic>(
            """
                SELECT
                    id, name, group_id
                FROM
                    topics
                WHERE
                    id < :afterId
                    ${query.names()}
                    ${query.groupIds()}
                ORDER BY id DESC
                LIMIT :limit
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
//                Topic(
//                    id = rs.getId("id", ::TopicId),
//                    name = TopicName(rs.getString("name")),
//                    groupId = rs.getId("group_id", ::GroupId)
//                )
                TODO()
            }
        }
    }

    fun count(query: TopicQueryRepository.TopicQuery): ULong {
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


    private fun TopicQueryRepository.TopicQuery.names(): String {
        return if (names.isEmpty()) {
            ""
        } else {
            "AND name IN (${names.joinToString(",") { "'${it.value}'" }})"
        }
    }

    private fun TopicQueryRepository.TopicQuery.groupIds(): String {
        return if (groupIds.isEmpty()) {
            ""
        } else {
            "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
        }
    }
}
