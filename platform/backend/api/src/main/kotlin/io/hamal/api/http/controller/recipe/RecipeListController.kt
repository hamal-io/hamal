package io.hamal.api.http.controller.recipe

import io.hamal.core.adapter.recipe.RecipeListPort
import io.hamal.core.component.Retry
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.sdk.api.ApiRecipeList
import io.hamal.lib.sdk.api.ApiRecipeList.Recipe
import io.hamal.repository.api.RecipeQueryRepository.RecipeQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class RecipeListController(
    private val recipeList: RecipeListPort,
    private val retry: Retry,
) {

    @GetMapping("/v1/recipes")
    fun listRecipes(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") recipeId: RecipeId,
        @RequestParam(required = false, name = "limit", defaultValue = "10") limit: Limit,
        @RequestParam(required = false, name = "recipe_ids", defaultValue = "") recipeIds: List<RecipeId>,
    ): ResponseEntity<ApiRecipeList> = retry {
        recipeList(
            RecipeQuery(
                afterId = recipeId,
                limit = limit,
                recipeIds = recipeIds
            )
        ).let { recipes ->
            ResponseEntity.ok(
                ApiRecipeList(
                    recipes.map { recipe ->
                        Recipe(
                            id = recipe.id,
                            name = recipe.name,
                            description = recipe.description
                        )
                    }
                )
            )

        }
    }
}


