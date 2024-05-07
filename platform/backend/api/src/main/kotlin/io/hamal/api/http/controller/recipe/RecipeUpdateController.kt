package io.hamal.api.http.controller.recipe

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.recipe.RecipeUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.sdk.api.ApiRecipeUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class RecipeUpdateController(
    private val retry: Retry,
    private val recipeUpdate: RecipeUpdatePort
) {
    @PatchMapping("/v1/recipes/{recipeId}")
    fun update(
        @PathVariable("recipeId") recipeId: RecipeId,
        @RequestBody req: ApiRecipeUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        recipeUpdate(recipeId, req).accepted()
    }
}