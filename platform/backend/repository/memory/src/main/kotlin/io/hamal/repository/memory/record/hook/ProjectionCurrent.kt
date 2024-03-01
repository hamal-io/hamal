package io.hamal.repository.memory.record.hook

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.Hook
import io.hamal.repository.api.HookQueryRepository.HookQuery

internal object HookCurrentProjection {
    private val projection = mutableMapOf<HookId, Hook>()
    fun apply(hook: Hook) {
        val currentHook = projection[hook.id]
        projection.remove(hook.id)

        val hooksInNamespace = projection.values.filter { it.namespaceId == hook.namespaceId }
        if (hooksInNamespace.any { it.name == hook.name }) {
            if (currentHook != null) {
                projection[currentHook.id] = currentHook
            }
            throw IllegalArgumentException("${hook.name} already exists in namespace ${hook.namespaceId}")
        }

        projection[hook.id] = hook
    }

    fun find(hookId: HookId): Hook? = projection[hookId]

    fun list(query: HookQuery): List<Hook> {
        return projection.filter { query.hookIds.isEmpty() || it.key in query.hookIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: HookQuery): Count {
        return Count(
            projection.filter { query.hookIds.isEmpty() || it.key in query.hookIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}
