package io.hamal.repository.memory.record.recipe

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeQueryRepository
import io.hamal.repository.memory.record.ProjectionMemory

internal class ProjectionCurrent : ProjectionMemory<RecipeId, Recipe> {

    override fun upsert(obj: Recipe) {
        projection[obj.id] = obj
    }

    fun find(recipeId: RecipeId): Recipe? = projection[recipeId]

    fun list(query: RecipeQueryRepository.RecipeQuery): List<Recipe> {
        return projection.filter { query.recipeIds.isEmpty() || it.key in query.recipeIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: RecipeQueryRepository.RecipeQuery): Count {
        return Count(
            projection.filter { query.recipeIds.isEmpty() || it.key in query.recipeIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    override fun clear() {
        projection.clear()
    }

    private val projection = mutableMapOf<RecipeId, Recipe>()
}
