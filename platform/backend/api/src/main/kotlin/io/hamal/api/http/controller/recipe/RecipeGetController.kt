package io.hamal.api.http.controller.recipe

import io.hamal.core.adapter.recipe.RecipeGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.sdk.api.ApiRecipe
import io.hamal.repository.api.Recipe
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class RecipeGetController(
    private val retry: Retry,
    private val getRecipe: RecipeGetPort
) {
    @GetMapping("/v1/recipes/{recipeId}")
    fun get(@PathVariable("recipeId") recipeId: RecipeId): ResponseEntity<ApiRecipe> = retry {
        assemble(getRecipe(recipeId))
    }

    private fun assemble(recipe: Recipe) =
        ResponseEntity.ok(
            ApiRecipe(
                id = recipe.id,
                name = recipe.name,
                inputs = recipe.inputs,
                value = recipe.value,
                description = recipe.description
            )
        )
}