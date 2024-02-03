package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository.CreateFlowTopicCmd
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
    private val projection = mutableMapOf<TopicId, Topic>()

    fun apply(topic: Topic.Flow) {
        val currentTopic = projection[topic.id]
        projection.remove(topic.id)

        val topicsInFlow = projection.values
            .filterIsInstance<Topic.Flow>()
            .filter { it.flowId == topic.flowId }

        if (topicsInFlow.any { it.name == topic.name }) {
            if (currentTopic != null) {
                projection[currentTopic.id] = currentTopic
            }
            throw IllegalArgumentException("${topic.name} already exists in flow ${topic.flowId}")
        }

        projection[topic.id] = topic
    }

    fun find(topicId: TopicId): Topic? = projection[topicId]

    fun list(query: TopicQueryRepository.TopicQuery): List<Topic> {
        TODO()
//        return projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }
//            .map { it.value }
//            .reversed()
//            .asSequence()
//            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
//            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
//            .dropWhile { it.id >= query.afterId }
//            .take(query.limit.value)
//            .toList()
    }

    fun count(query: TopicQueryRepository.TopicQuery): ULong {
        TODO()
//        return projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }
//            .map { it.value }
//            .reversed()
//            .asSequence()
//            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
//            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
//            .dropWhile { it.id >= query.afterId }
//            .count()
//            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class TopicMemoryRepository(
    val logBrokerRepository: LogBrokerRepository
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
                    name = cmd.name,
                )
            )
            (currentVersion(topicId) as Topic.Flow)
                .also(TopicCurrentProjection::apply)
                .also { _ ->
                    logBrokerRepository.create(
                        cmd.id, LogTopicToCreate(
                            id = cmd.logTopicId
                        )
                    )
                }
        }
    }

    override fun close() {}

    override fun find(topicId: TopicId): Topic? = lock.withLock { TopicCurrentProjection.find(topicId) }

    override fun list(query: TopicQueryRepository.TopicQuery): List<Topic> =
        lock.withLock { TopicCurrentProjection.list(query) }

    override fun count(query: TopicQueryRepository.TopicQuery): ULong =
        lock.withLock { TopicCurrentProjection.count(query) }

    private val lock = ReentrantLock()
}

