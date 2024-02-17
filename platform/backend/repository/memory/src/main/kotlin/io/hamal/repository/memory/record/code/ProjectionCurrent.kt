package io.hamal.repository.memory.record.code

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.CodeId
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository

internal object CodeCurrentProjection {
    private val projection = mutableMapOf<CodeId, Code>()
    fun apply(code: Code) {
        projection[code.id] = code
    }

    fun find(codeId: CodeId): Code? = projection[codeId]

    fun list(query: CodeQueryRepository.CodeQuery): List<Code> {
        return projection.filter { query.codeIds.isEmpty() || it.key in query.codeIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: CodeQueryRepository.CodeQuery): Count {
        return Count(
            projection.filter { query.codeIds.isEmpty() || it.key in query.codeIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter {
                    if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
                }.dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}
