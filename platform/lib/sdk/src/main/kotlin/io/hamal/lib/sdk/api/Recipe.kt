package io.hamal.lib.sdk.api

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.RecipeCreateRequest
import io.hamal.lib.domain.request.RecipeUpdateRequest
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold

data class ApiRecipe(
    val id: RecipeId,
    val name: RecipeName,
    val inputs: RecipeInputs,
    val value: ValueCode,
    val description: RecipeDescription
) : ApiObject()

data class ApiRecipeCreateRequest(
    override val name: RecipeName,
    override val inputs: RecipeInputs,
    override val value: ValueCode,
    override val description: RecipeDescription? = null
) : RecipeCreateRequest

data class ApiRecipeCreateRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: RecipeId
) : ApiRequested()

data class ApiRecipeUpdateRequest(
    override val name: RecipeName? = null,
    override val inputs: RecipeInputs? = null,
    override val value: ValueCode? = null,
    override val description: RecipeDescription? = null
) : RecipeUpdateRequest

data class ApiRecipeUpdateRequested(
    override val requestId: RequestId,
    override val requestStatus: RequestStatus,
    val id: RecipeId
) : ApiRequested()

data class ApiRecipeList(
    val recipes: List<Recipe>
) : ApiObject() {
    data class Recipe(
        val id: RecipeId,
        val name: RecipeName,
        val description: RecipeDescription
    )
}

interface ApiRecipeService {
    fun create(req: ApiRecipeCreateRequest): ApiRecipeCreateRequested
    fun get(recipeId: RecipeId): ApiRecipe
    fun update(recipeId: RecipeId, req: ApiRecipeUpdateRequest): ApiRecipeUpdateRequested
}

internal class ApiRecipeServiceImpl(
    private val template: HttpTemplate
) : ApiRecipeService {

    override fun create(req: ApiRecipeCreateRequest): ApiRecipeCreateRequested =
        template.post("/v1/recipes")
            .body(req)
            .execute()
            .fold(ApiRecipeCreateRequested::class)


    override fun get(recipeId: RecipeId): ApiRecipe =
        template.get("/v1/recipes/{recipeId}")
            .path("recipeId", recipeId)
            .execute()
            .fold(ApiRecipe::class)


    override fun update(recipeId: RecipeId, req: ApiRecipeUpdateRequest): ApiRecipeUpdateRequested =
        template.patch("/v1/recipes/{recipeId}")
            .path("recipeId", recipeId)
            .body(req)
            .execute()
            .fold(ApiRecipeUpdateRequested::class)
}



