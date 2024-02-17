package io.hamal.repository.memory.record.func

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository.FuncQuery

internal object FuncCurrentProjection {
    private val projection = mutableMapOf<FuncId, Func>()
    fun apply(func: Func) {
        val currentFunc = projection[func.id]
        projection.remove(func.id)

        val funcsInNamespace = projection.values.filter { it.namespaceId == func.namespaceId }
        if (funcsInNamespace.any { it.name == func.name }) {
            if (currentFunc != null) {
                projection[currentFunc.id] = currentFunc
            }
            throw IllegalArgumentException("${func.name} already exists in namespace ${func.namespaceId}")
        }

        projection[func.id] = func
    }

    fun find(funcId: FuncId): Func? = projection[funcId]

    fun list(query: FuncQuery): List<Func> {
        return projection.filter { query.funcIds.isEmpty() || it.key in query.funcIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: FuncQuery): Count {
        return Count(
            projection.filter { query.funcIds.isEmpty() || it.key in query.funcIds }
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