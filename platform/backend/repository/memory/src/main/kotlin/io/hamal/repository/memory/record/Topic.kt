package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository.CreateFlowTopicCmd
import io.hamal.repository.api.TopicEntry
import io.hamal.repository.api.TopicQueryRepository
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.new_log.LogBrokerRepository
import io.hamal.repository.api.new_log.LogBrokerRepository.LogTopicToCreate
import io.hamal.repository.record.topic.CreateTopicFromRecords
import io.hamal.repository.record.topic.TopicFlowCreatedRecord
import io.hamal.repository.record.topic.TopicRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object TopicCurrentProjection {

    fun apply(topic: Topic.Flow) {
        val currentTopic = projection[topic.id]
        projection.remove(topic.id)

        val topicsInFlow = projection.values
            .filterIsInstance<Topic.Flow>()
            .filter { it.groupId == topic.groupId }

        if (topicsInFlow.any { it.name == topic.name }) {
            if (currentTopic != null) {
                projection[currentTopic.id] = currentTopic
            }
            throw IllegalArgumentException("${topic.name} already exists")
        }

        projection[topic.id] = topic
    }

    fun find(topicId: TopicId): Topic? = projection[topicId]

    fun list(query: TopicQueryRepository.TopicQuery): List<Topic> {
        return projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
            .filter {
                if (query.groupIds.isEmpty()) {
                    true
                } else {
                    if (it is Topic.Flow) {
                        query.groupIds.contains(it.groupId)
                    } else {
                        false
                    }
                }
            }
            .filter {
                if (query.flowIds.isEmpty()) {
                    true
                } else {
                    if (it is Topic.Flow) {
                        query.flowIds.contains(it.flowId)
                    } else {
                        false
                    }
                }
            }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: TopicQueryRepository.TopicQuery): Count {
        return Count(
            projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
                .filter {
                    if (query.groupIds.isEmpty()) {
                        true
                    } else {
                        if (it is Topic.Flow) {
                            query.groupIds.contains(it.groupId)
                        } else {
                            false
                        }
                    }
                }
                .filter {
                    if (query.flowIds.isEmpty()) {
                        true
                    } else {
                        if (it is Topic.Flow) {
                            query.flowIds.contains(it.flowId)
                        } else {
                            false
                        }
                    }
                }
                .dropWhile { it.id >= query.afterId }
                .count()
        )
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

    override fun create(cmd: CreateFlowTopicCmd): Topic.Flow {
        return lock.withLock {
            val topicId = cmd.topicId
            if (commandAlreadyApplied(cmd.id, topicId)) {
                versionOf(topicId, cmd.id) as Topic.Flow
            }
            store(
                TopicFlowCreatedRecord(
                    cmdId = cmd.id,
                    entityId = topicId,
                    groupId = cmd.groupId,
                    flowId = cmd.flowId,
                    logTopicId = cmd.logTopicId,
                    name = cmd.name,
                )
            )
            (currentVersion(topicId) as Topic.Flow)
                .also(TopicCurrentProjection::apply)
                .also { _ ->
                    logBrokerRepository.create(cmd.id, LogTopicToCreate(cmd.logTopicId))
                }
        }
    }

    override fun close() {}

    override fun find(topicId: TopicId): Topic? = lock.withLock { TopicCurrentProjection.find(topicId) }

    override fun list(query: TopicQueryRepository.TopicQuery): List<Topic> =
        lock.withLock { TopicCurrentProjection.list(query) }

    override fun list(query: TopicQueryRepository.TopicEntryQuery): List<TopicEntry> {
        TODO("Not yet implemented")
    }

    override fun count(query: TopicQueryRepository.TopicQuery): Count =
        lock.withLock { TopicCurrentProjection.count(query) }

    override fun count(query: TopicQueryRepository.TopicEntryQuery): Count {
        TODO("Not yet implemented")
    }

    override fun clear() {
        lock.withLock {
            TopicCurrentProjection.clear()
        }
    }

    private val lock = ReentrantLock()
}

