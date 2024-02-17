package io.hamal.repository.memory.record.exec

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository.ExecQuery

internal object ExecCurrentProjection {
    private val projection = mutableMapOf<ExecId, Exec>()
    fun apply(exec: Exec) {
        projection[exec.id] = exec
    }

    fun find(execId: ExecId): Exec? = projection[execId]

    fun list(query: ExecQuery): List<Exec> {
        return projection.filter { query.execIds.isEmpty() || it.key in query.execIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.funcIds.isEmpty()) true else (it.correlation != null && query.funcIds.contains(it.correlation!!.funcId)) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: ExecQuery): Count {
        return Count(
            projection.filter { query.execIds.isEmpty() || it.key in query.execIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .filter { if (query.funcIds.isEmpty()) true else (it.correlation != null && query.funcIds.contains(it.correlation!!.funcId)) }
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

//FIXME this must be concurrent extend
internal object QueueProjection {
    private val queue = mutableListOf<Exec.Queued>()
    fun add(exec: Exec.Queued) {
        queue.add(exec)
    }

    fun pop(limit: Int): List<Exec.Queued> {
        val result = mutableListOf<Exec.Queued>()
        for (idx in 0 until limit) {
            if (queue.isEmpty()) {
                break
            }
            result.add(queue.removeFirst())
        }
        return result
    }

    fun clear() {
        queue.clear()
    }
}
