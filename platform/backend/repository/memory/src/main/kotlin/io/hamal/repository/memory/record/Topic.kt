package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository.CreateFlowTopicCmd
import io.hamal.repository.api.TopicQueryRepository
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.new_log.LogBrokerRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.topic.CreateTopicFromRecords
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.record.topic.createEntity

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
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun find(topicId: TopicId): Topic? {
        TODO("Not yet implemented")
    }

    override fun list(query: TopicQueryRepository.TopicQuery): List<Topic> {
        TODO("Not yet implemented")
    }

    override fun count(query: TopicQueryRepository.TopicQuery): ULong {
        TODO("Not yet implemented")
    }

}

object CreateTopicFromRecords : CreateDomainObject<TopicId, TopicRecord, Topic> {
    override fun invoke(recs: List<TopicRecord>): Topic {
        return recs.createEntity().toDomainObject()
    }
}