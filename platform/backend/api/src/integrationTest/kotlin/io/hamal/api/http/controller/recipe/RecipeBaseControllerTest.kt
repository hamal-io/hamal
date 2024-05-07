package io.hamal.api.http.controller.recipe

import io.hamal.api.http.controller.BaseControllerTest
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.http.HttpStatusCode.Accepted
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiRecipe
import io.hamal.lib.sdk.api.ApiRecipeCreateRequest
import io.hamal.lib.sdk.api.ApiRecipeCreateRequested
import io.hamal.lib.sdk.api.ApiRecipeList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class RecipeBaseControllerTest : BaseControllerTest() {
    fun createRecipe(req: ApiRecipeCreateRequest): ApiRecipeCreateRequested {
        val createRecipeResponse = httpTemplate.post("/v1/recipes")
            .body(req)
            .execute()

        assertThat(createRecipeResponse.statusCode, equalTo(Accepted))
        require(createRecipeResponse is HttpSuccessResponse) { "request was not successful" }
        return createRecipeResponse.result(ApiRecipeCreateRequested::class)

    }

    fun getRecipe(recipeId: RecipeId): ApiRecipe {
        val getRecipeResponse = httpTemplate.get("/v1/recipes/{recipeId}")
            .path("recipeId", recipeId)
            .execute()

        assertThat(getRecipeResponse.statusCode, equalTo(Ok))
        require(getRecipeResponse is HttpSuccessResponse) { "request was not successful" }
        return getRecipeResponse.result(ApiRecipe::class)
    }

    fun listRecipe(): ApiRecipeList {
        val listRecipeResponse = httpTemplate.get("/v1/recipes")
            .execute()

        assertThat(listRecipeResponse.statusCode, equalTo(Ok))
        require(listRecipeResponse is HttpSuccessResponse) { "request was not successful" }
        return listRecipeResponse.result(ApiRecipeList::class)
    }
}