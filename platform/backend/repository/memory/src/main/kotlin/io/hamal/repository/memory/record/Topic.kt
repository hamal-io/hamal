package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository
import io.hamal.repository.api.TopicCmdRepository.TopicGroupCreateCmd
import io.hamal.repository.api.TopicEvent
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.LogTopicToCreate
import io.hamal.repository.api.log.LogEventId
import io.hamal.repository.record.json
import io.hamal.repository.record.topic.CreateTopicFromRecords
import io.hamal.repository.record.topic.TopicGroupCreatedRecord
import io.hamal.repository.record.topic.TopicInternalCreatedRecord
import io.hamal.repository.record.topic.TopicRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object TopicCurrentProjection {

    fun apply(topic: Topic) {
        val currentTopic = projection[topic.id]
        projection.remove(topic.id)

        val topicsInGroup = projection.values.filter { it.groupId == topic.groupId }

        if (topicsInGroup.any { it.name == topic.name }) {
            if (currentTopic != null) {
                projection[currentTopic.id] = currentTopic
            }
            throw IllegalArgumentException("Topic already exists")
        }

        projection[topic.id] = topic
    }

    fun find(topicId: TopicId): Topic? = projection[topicId]

    fun find(groupId: GroupId, topicName: TopicName): Topic? = projection.values.find {
        it.groupId == groupId && it.name == topicName
    }

    fun list(query: TopicQuery): List<Topic> {
        return projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }.map { it.value }.reversed()
            .asSequence().filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }.filter {
                if (query.groupIds.isEmpty()) {
                    true
                } else {
                    if (it is Topic.Group) {
                        query.groupIds.contains(it.groupId)
                    } else {
                        false
                    }
                }
            }.dropWhile { it.id >= query.afterId }.take(query.limit.value).toList()
    }

    fun count(query: TopicQuery): Count {
        return Count(projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }.map { it.value }
            .reversed().asSequence().filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }.filter {
                if (query.groupIds.isEmpty()) {
                    true
                } else {
                    if (it is Topic.Group) {
                        query.groupIds.contains(it.groupId)
                    } else {
                        false
                    }
                }
            }.dropWhile { it.id >= query.afterId }.count())
    }

    fun clear() {
        projection.clear()
    }

    private val projection = mutableMapOf<TopicId, Topic>()
}

class TopicMemoryRepository(
    private val logBrokerRepository: LogBrokerRepository
) : RecordMemoryRepository<TopicId, TopicRecord, Topic>(
    createDomainObject = CreateTopicFromRecords,
    recordClass = TopicRecord::class
), TopicRepository {

    override fun create(cmd: TopicGroupCreateCmd): Topic.Group {
        return lock.withLock {
            val topicId = cmd.topicId
            if (commandAlreadyApplied(cmd.id, topicId)) {
                versionOf(topicId, cmd.id) as Topic.Group
            }
            store(
                TopicGroupCreatedRecord(
                    cmdId = cmd.id,
                    entityId = topicId,
                    groupId = cmd.groupId,
                    logTopicId = cmd.logTopicId,
                    name = cmd.name,
                )
            )
            (currentVersion(topicId) as Topic.Group).also(TopicCurrentProjection::apply).also { _ ->
                logBrokerRepository.create(cmd.id, LogTopicToCreate(cmd.logTopicId))
            }
        }
    }

    override fun create(cmd: TopicCmdRepository.TopicInternalCreateCmd): Topic.Internal {
        return lock.withLock {
            val topicId = cmd.topicId
            if (commandAlreadyApplied(cmd.id, topicId)) {
                versionOf(topicId, cmd.id) as Topic.Internal
            }
            store(
                TopicInternalCreatedRecord(
                    cmdId = cmd.id,
                    entityId = topicId,
                    logTopicId = cmd.logTopicId,
                    name = cmd.name,
                    groupId = GroupId.root
                )
            )
            (currentVersion(topicId) as Topic.Internal).also(TopicCurrentProjection::apply).also { _ ->
                logBrokerRepository.create(cmd.id, LogTopicToCreate(cmd.logTopicId))
            }
        }
    }

    override fun close() {}

    override fun find(topicId: TopicId): Topic? = lock.withLock { TopicCurrentProjection.find(topicId) }

    override fun findGroupTopic(groupId: GroupId, topicName: TopicName): Topic? = lock.withLock {
        TopicCurrentProjection.find(groupId, topicName)
    }

    override fun list(query: TopicQuery): List<Topic> =
        lock.withLock { TopicCurrentProjection.list(query) }

    override fun list(query: TopicEventQuery): List<TopicEvent> {
        return lock.withLock {
            val topic = get(query.topicId)
            logBrokerRepository.read(
                firstId = LogEventId(SnowflakeId(query.afterId.value.value + 1)),
                topicId = topic.logTopicId,
                limit = query.limit
            ).map { evt ->
                TopicEvent(
                    id = TopicEventId(evt.id.value),
                    payload = TopicEventPayload(json.decompressAndDeserialize(HotObject::class, evt.bytes))
                )
            }
        }
    }

    override fun count(query: TopicQuery): Count =
        lock.withLock { TopicCurrentProjection.count(query) }

    override fun count(query: TopicEventQuery): Count {
        TODO("Not yet implemented")
    }

    override fun clear() {
        lock.withLock {
            TopicCurrentProjection.clear()
        }
    }

    private val lock = ReentrantLock()
}

