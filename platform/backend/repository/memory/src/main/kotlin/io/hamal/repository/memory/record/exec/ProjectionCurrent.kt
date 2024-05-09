package io.hamal.repository.memory.record.exec

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain.vo.ExecId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory.BaseImpl<ExecId, Exec>() {

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
            .take(query.limit.intValue)
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

}

internal class ProjectionQueue : ProjectionMemory<ExecId, Exec.Queued> {

    override fun upsert(obj: Exec.Queued) {
        queue.add(obj)
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

    override fun delete(id: ExecId) {
        queue.removeIf { it.id == id }
    }

    override fun clear() {
        queue.clear()
    }

    private val queue = mutableListOf<Exec.Queued>()
}
