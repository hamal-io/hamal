package io.hamal.repository.memory.record.topic

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository.TopicCreateCmd
import io.hamal.repository.api.TopicEvent
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.CreateTopicCmd
import io.hamal.repository.api.log.LogEventId.Companion.LogEventId
import io.hamal.repository.memory.hon
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.topic.CreateTopicFromRecords
import io.hamal.repository.record.topic.TopicRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TopicMemoryRepository(
    private val logBrokerRepository: LogBrokerRepository
) : RecordMemoryRepository<TopicId, TopicRecord, Topic>(
    createDomainObject = CreateTopicFromRecords,
    recordClass = TopicRecord::class,
    projections = listOf(ProjectionCurrent())
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
                    workspaceId = cmd.workspaceId,
                    namespaceId = cmd.namespaceId,
                    logTopicId = cmd.logTopicId,
                    name = cmd.name,
                    type = cmd.type
                )
            )
            currentVersion(topicId).also(currentProjection::upsert).also { _ ->
                logBrokerRepository.create(CreateTopicCmd(cmd.id, cmd.logTopicId))
            }
        }
    }


    override fun find(topicId: TopicId): Topic? = lock.withLock { currentProjection.find(topicId) }

    override fun findTopic(namespaceId: NamespaceId, topicName: TopicName): Topic? = lock.withLock {
        currentProjection.find(namespaceId, topicName)
    }

    override fun list(query: TopicQuery): List<Topic> =
        lock.withLock { currentProjection.list(query) }

    override fun list(query: TopicEventQuery): List<TopicEvent> {
        return lock.withLock {
            val topic = get(query.topicId)
            logBrokerRepository.read(
                firstId = LogEventId(SnowflakeId(query.afterId.longValue + 1)),
                topicId = topic.logTopicId,
                limit = query.limit
            ).map { evt ->
                TopicEvent(
                    id = TopicEventId(evt.id.value),
                    payload = TopicEventPayload(hon.decompressAndRead(ValueObject::class, evt.bytes))
                )
            }
        }
    }

    override fun count(query: TopicQuery): Count = lock.withLock { currentProjection.count(query) }

    override fun count(query: TopicEventQuery): Count {
        TODO("Not yet implemented")
    }

    override fun close() {}

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()
}

