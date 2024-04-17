package io.hamal.api.http.controller.recipe

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode.NotFound
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiRecipe
import io.hamal.lib.sdk.api.ApiRecipeCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class RecipeGetControllerTest : RecipeBaseControllerTest() {

    @Test
    fun `Get Recipe`() {
        val recipeId = awaitCompleted(
            createRecipe(
                ApiRecipeCreateRequest(
                    name = RecipeName("TestRecipe"),
                    inputs = RecipeInputs(HotObject.builder().set("hamal", "rocks").build()),
                    value = CodeValue("1 + 1")
                )
            )
        ).id

        val getResponse = httpTemplate.get("/v1/recipes/{recipeId}")
            .path("recipeId", recipeId)
            .execute()


        assertThat(getResponse.statusCode, equalTo(Ok))
        require(getResponse is HttpSuccessResponse) { "request was not successful" }

        with(getResponse.result(ApiRecipe::class)) {
            assertThat(id, equalTo(recipeId))
            assertThat(name, equalTo(RecipeName("TestRecipe")))
            assertThat(inputs, equalTo(RecipeInputs(HotObject.builder().set("hamal", "rocks").build())))
            assertThat(value, equalTo(CodeValue("1 + 1")))
        }
    }

    @Test
    fun `Tries to get Recipe that does not exist`() {
        val getResponse = httpTemplate.get("/v1/recipes/33333333").execute()
        assertThat(getResponse.statusCode, equalTo(NotFound))
        require(getResponse is HttpErrorResponse) { "request was successful" }

        val error = getResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Recipe not found"))
    }
}