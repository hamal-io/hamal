package io.hamal.repository.memory.record.topic

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicQueryRepository.TopicQuery

internal object TopicCurrentProjection {

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

    fun clear() {
        projection.clear()
    }

    private val projection = mutableMapOf<TopicId, Topic>()
}

