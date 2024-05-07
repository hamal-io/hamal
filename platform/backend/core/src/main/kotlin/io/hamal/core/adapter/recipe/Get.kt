package io.hamal.core.adapter.recipe

import io.hamal.lib.domain.vo.RecipeId
import io.hamal.repository.api.Recipe
import io.hamal.repository.api.RecipeQueryRepository
import org.springframework.stereotype.Component

fun interface RecipeGetPort {
    operator fun invoke(recipeId: RecipeId): Recipe
}

@Component
class RecipeGetAdapter(
    private val recipeQueryRepository: RecipeQueryRepository
) : RecipeGetPort {
    override fun invoke(recipeId: RecipeId): Recipe = recipeQueryRepository.get(recipeId)
}
