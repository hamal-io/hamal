package io.hamal.repository.memory.record.trigger

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery


internal object TriggerCurrentProjection {

    private val projection = mutableMapOf<TriggerId, Trigger>()
    private val uniqueHookTriggers = mutableSetOf<HookTriggerUnique>()

    fun apply(trigger: Trigger) {

        if (trigger.type == TriggerType.Hook) {
            handleHookTrigger(trigger as Trigger.Hook)
        }

        val currentTrigger = projection[trigger.id]
        projection.remove(trigger.id)

        val triggersInNamespace = projection.values.filter { it.namespaceId == trigger.namespaceId }
        if (triggersInNamespace.any { it.name == trigger.name }) {
            if (currentTrigger != null) {
                projection[currentTrigger.id] = currentTrigger
            }
            throw IllegalArgumentException("${trigger.name} already exists in namespace ${trigger.namespaceId}")
        }

        projection[trigger.id] = trigger
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
            .filter {
                if (query.hookIds.isEmpty()) {
                    true
                } else {
                    if (it is Trigger.Hook) {
                        query.hookIds.contains(it.hookId)
                    } else {
                        false
                    }
                }
            }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
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
                .filter {
                    if (query.hookIds.isEmpty()) {
                        true
                    } else {
                        if (it is Trigger.Hook) {
                            query.hookIds.contains(it.hookId)
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

    fun clear() {
        projection.clear()
        uniqueHookTriggers.clear()
    }

    private fun handleHookTrigger(trigger: Trigger.Hook) {
        val toCheck = HookTriggerUnique(
            trigger.funcId,
            trigger.hookId,
            trigger.hookMethod
        )
        require(uniqueHookTriggers.add(toCheck)) { "Trigger already exists" }
    }

    private data class HookTriggerUnique(
        val funcId: FuncId,
        val hookId: HookId,
        val hookMethod: HookMethod
    )
}
