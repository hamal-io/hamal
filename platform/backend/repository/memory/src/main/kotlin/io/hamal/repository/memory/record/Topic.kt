package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository.TopicCreateCmd
import io.hamal.repository.api.TopicEvent
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.LogTopicToCreate
import io.hamal.repository.api.log.LogEventId
import io.hamal.repository.record.json
import io.hamal.repository.record.topic.CreateTopicFromRecords
import io.hamal.repository.record.topic.TopicRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object TopicCurrentProjection {

    fun apply(topic: Topic) {
        val currentTopic = projection[topic.id]
        projection.remove(topic.id)

        val topicsInNamespace = projection.values.filter { it.namespaceId == topic.namespaceId }

        if (topicsInNamespace.any { it.name == topic.name }) {
            if (currentTopic != null) {
                projection[currentTopic.id] = currentTopic
            }
            throw IllegalArgumentException("Topic already exists")
        }

        projection[topic.id] = topic
    }

    fun find(topicId: TopicId): Topic? = projection[topicId]

    fun find(namespaceId: NamespaceId, topicName: TopicName): Topic? = projection.values.find {
        it.namespaceId == namespaceId && it.name == topicName
    }

    fun list(query: TopicQuery): List<Topic> {
        return projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }.map { it.value }.reversed()
            .asSequence().filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
            .filter { if (query.groupIds.isEmpty()) { true } else { query.groupIds.contains(it.groupId) } }
            .filter { if (query.namespaceIds.isEmpty()) { true } else { query.namespaceIds.contains(it.namespaceId) } }
            .dropWhile { it.id >= query.afterId }.take(query.limit.value).toList()
    }

    fun count(query: TopicQuery): Count {
        return Count(projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }.map { it.value }
            .reversed().asSequence().filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
            .filter { if (query.groupIds.isEmpty()) { true } else { query.groupIds.contains(it.groupId) } }
            .filter { if (query.namespaceIds.isEmpty()) { true } else { query.namespaceIds.contains(it.namespaceId) } }
            .dropWhile { it.id >= query.afterId }.count())
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

    override fun create(cmd: TopicCreateCmd): Topic {
        return lock.withLock {
            val topicId = cmd.topicId
            if (commandAlreadyApplied(cmd.id, topicId)) {
                versionOf(topicId, cmd.id)
            }
            store(
                TopicRecord.Created(
                    cmdId = cmd.id,
                    entityId = topicId,
                    groupId = cmd.groupId,
                    namespaceId = cmd.namespaceId,
                    logTopicId = cmd.logTopicId,
                    name = cmd.name,
                    type = cmd.type
                )
            )
            currentVersion(topicId).also(TopicCurrentProjection::apply).also { _ ->
                logBrokerRepository.create(cmd.id, LogTopicToCreate(cmd.logTopicId))
            }
        }
    }

    override fun close() {}

    override fun find(topicId: TopicId): Topic? = lock.withLock { TopicCurrentProjection.find(topicId) }

    override fun findTopic(namespaceId: NamespaceId, topicName: TopicName): Topic? = lock.withLock {
        TopicCurrentProjection.find(namespaceId, topicName)
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

