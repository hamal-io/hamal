package io.hamal.core.adapter.recipe

import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeQueryRepository
import io.hamal.repository.api.RecipeQueryRepository.RecipeQuery
import org.springframework.stereotype.Component

fun interface RecipeListPort {
    operator fun invoke(query: RecipeQuery): List<Recipe>
}

@Component
class RecipeListAdapter(
    private val recipeQueryRepository: RecipeQueryRepository
) : RecipeListPort {
    override fun invoke(query: RecipeQuery): List<Recipe> = recipeQueryRepository.list(query)
}