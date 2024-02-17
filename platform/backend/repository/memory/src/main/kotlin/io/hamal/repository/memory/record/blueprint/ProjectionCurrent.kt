package io.hamal.repository.memory.record.blueprint

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.repository.api.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository

internal object BlueprintCurrentProjection {
    private val projection = mutableMapOf<BlueprintId, Blueprint>()
    fun apply(blueprint: Blueprint) {
        projection[blueprint.id] = blueprint
    }

    fun find(blueprintId: BlueprintId): Blueprint? = projection[blueprintId]

    fun list(query: BlueprintQueryRepository.BlueprintQuery): List<Blueprint> {
        return projection.filter { query.blueprintIds.isEmpty() || it.key in query.blueprintIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: BlueprintQueryRepository.BlueprintQuery): Count {
        return Count(
            projection.filter { query.blueprintIds.isEmpty() || it.key in query.blueprintIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}
