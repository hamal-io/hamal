package io.hamal.repository.memory.record.func

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.FuncId
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory.BaseImpl<FuncId, Func>() {

    override fun upsert(obj: Func) {
        val currentFunc = projection[obj.id]
        projection.remove(obj.id)

        val funcsInNamespace = projection.values.filter { it.namespaceId == obj.namespaceId }
        if (funcsInNamespace.any { it.name == obj.name }) {
            if (currentFunc != null) {
                projection[currentFunc.id] = currentFunc
            }
            throw IllegalArgumentException("${obj.name} already exists in namespace ${obj.namespaceId}")
        }

        projection[obj.id] = obj
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
            .take(query.limit.intValue)
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
}