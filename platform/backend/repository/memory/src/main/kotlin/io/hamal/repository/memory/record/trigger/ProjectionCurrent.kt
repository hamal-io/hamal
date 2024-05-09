package io.hamal.repository.memory.record.trigger

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.memory.record.ProjectionMemory


internal class ProjectionCurrent : ProjectionMemory.BaseImpl<TriggerId, Trigger>() {

    override fun upsert(obj: Trigger) {
        val currentTrigger = projection[obj.id]
        projection.remove(obj.id)

        val triggersInNamespace = projection.values.filter { it.namespaceId == obj.namespaceId }
        if (triggersInNamespace.any { it.name == obj.name }) {
            if (currentTrigger != null) {
                projection[currentTrigger.id] = currentTrigger
            }
            throw IllegalArgumentException("${obj.name} already exists in namespace ${obj.namespaceId}")
        }

        projection[obj.id] = obj
    }

    fun find(triggerId: TriggerId): Trigger? = projection[triggerId]

    fun list(query: TriggerQuery): List<Trigger> {
        return projection.filter { query.triggerIds.isEmpty() || it.key in query.triggerIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.funcIds.isEmpty()) true else query.funcIds.contains(it.funcId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .filter {
                if (query.topicIds.isEmpty()) {
                    true
                } else {
                    if (it is Trigger.Event) {
                        query.topicIds.contains(it.topicId)
                    } else {
                        false
                    }
                }
            }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.intValue)
            .toList()
    }

    fun count(query: TriggerQuery): Count {
        return Count(
            projection.filter { query.triggerIds.isEmpty() || it.key in query.triggerIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.types.isEmpty()) true else query.types.contains(it.type) }
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .filter { if (query.funcIds.isEmpty()) true else query.funcIds.contains(it.funcId) }
                .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
                .filter {
                    if (query.topicIds.isEmpty()) {
                        true
                    } else {
                        if (it is Trigger.Event) {
                            query.topicIds.contains(it.topicId)
                        } else {
                            false
                        }
                    }
                }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

}
