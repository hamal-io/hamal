package io.hamal.api.http.controller.recipe

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.recipe.RecipeCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.AccountId.Companion.AccountId
import io.hamal.lib.sdk.api.ApiRecipeCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipeCreateController(
    private val retry: Retry,
    private val recipeCreate: RecipeCreatePort
) {
    @PostMapping("/v1/recipes")
    fun createRecipe(
        @RequestBody req: ApiRecipeCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        // 227 - FIXME remove hardcode account id
        recipeCreate(AccountId(1), req).accepted()
    }
}