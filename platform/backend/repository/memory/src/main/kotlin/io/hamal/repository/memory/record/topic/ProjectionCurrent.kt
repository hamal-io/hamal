package io.hamal.repository.memory.record.topic

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory<TopicId, Topic>{

    override fun upsert(obj: Topic) {
        val currentTopic = projection[obj.id]
        projection.remove(obj.id)

        val topicsInNamespace = projection.values.filter { it.namespaceId == obj.namespaceId }

        if (topicsInNamespace.any { it.name == obj.name }) {
            if (currentTopic != null) {
                projection[currentTopic.id] = currentTopic
            }
            throw IllegalArgumentException("Topic already exists")
        }

        projection[obj.id] = obj
    }

    fun find(topicId: TopicId): Topic? = projection[topicId]

    fun find(namespaceId: NamespaceId, topicName: TopicName): Topic? = projection.values.find {
        it.namespaceId == namespaceId && it.name == topicName
    }

    fun list(query: TopicQuery): List<Topic> {
        return projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }.map { it.value }.reversed()
            .asSequence().filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
            .filter {
                if (query.workspaceIds.isEmpty()) {
                    true
                } else {
                    query.workspaceIds.contains(it.workspaceId)
                }
            }
            .filter {
                if (query.namespaceIds.isEmpty()) {
                    true
                } else {
                    query.namespaceIds.contains(it.namespaceId)
                }
            }
            .dropWhile { it.id >= query.afterId }.take(query.limit.value).toList()
    }

    fun count(query: TopicQuery): Count {
        return Count(projection.filter { query.topicIds.isEmpty() || it.key in query.topicIds }.map { it.value }
            .reversed().asSequence().filter { if (query.names.isEmpty()) true else query.names.contains(it.name) }
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
            .filter {
                if (query.workspaceIds.isEmpty()) {
                    true
                } else {
                    query.workspaceIds.contains(it.workspaceId)
                }
            }
            .filter {
                if (query.namespaceIds.isEmpty()) {
                    true
                } else {
                    query.namespaceIds.contains(it.namespaceId)
                }
            }
            .dropWhile { it.id >= query.afterId }.count())
    }

    override fun clear() {
        projection.clear()
    }

    private val projection = mutableMapOf<TopicId, Topic>()
}

