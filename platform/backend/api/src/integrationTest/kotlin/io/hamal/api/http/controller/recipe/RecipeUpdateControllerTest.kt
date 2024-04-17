package io.hamal.api.http.controller.recipe

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.RecipeDescription
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName
import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiError
import io.hamal.lib.sdk.api.ApiRecipeCreateRequest
import io.hamal.lib.sdk.api.ApiRecipeUpdateRequest
import io.hamal.lib.sdk.api.ApiRecipeUpdateRequested
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class RecipeUpdateControllerTest : RecipeBaseControllerTest() {

    @Test
    fun `Updates Recipe`() {
        val recipe = awaitCompleted(
            createRecipe(
                ApiRecipeCreateRequest(
                    name = RecipeName("TestRecipe"),
                    value = CodeValue("40 + 2"),
                    inputs = RecipeInputs()
                )
            )
        )

        val updateResponse = httpTemplate.patch("/v1/recipes/{recipeId}")
            .path("recipeId", recipe.id)
            .body(
                ApiRecipeUpdateRequest(
                    name = RecipeName("Other"),
                    value = CodeValue("1 + 1"),
                    inputs = RecipeInputs(HotObject.builder().set("hamal", "createdInputs").build()),
                    description = RecipeDescription("updated description")
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateResponse.result(ApiRecipeUpdateRequested::class)
        awaitCompleted(submittedReq)
        val recipeId = submittedReq.id

        with(getRecipe(recipeId)) {
            assertThat(id, equalTo(recipeId))
            assertThat(name, equalTo(RecipeName("Other")))
            assertThat(value, equalTo(CodeValue("1 + 1")))
            assertThat(inputs, equalTo(RecipeInputs(HotObject.builder().set("hamal", "createdInputs").build())))
            assertThat(description, equalTo(RecipeDescription("updated description")))
        }
    }

    @Test
    fun `Updates Recipe without updating values`() {
        val recipe = awaitCompleted(
            createRecipe(
                ApiRecipeCreateRequest(
                    name = RecipeName("TestRecipe"),
                    value = CodeValue("40 + 2"),
                    inputs = RecipeInputs(HotObject.builder().set("hamal", "createdInputs").build())
                )
            )
        )

        val updateResponse = httpTemplate.patch("/v1/recipes/{recipeId}")
            .path("recipeId", recipe.id)
            .body(
                ApiRecipeUpdateRequest(
                    name = null,
                    value = null,
                    inputs = null
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(updateResponse is HttpSuccessResponse) { "request was not successful" }

        val submittedReq = updateResponse.result(ApiRecipeUpdateRequested::class)
        awaitCompleted(submittedReq)

        val bpId = submittedReq.id

        with(getRecipe(bpId)) {
            assertThat(id, equalTo(bpId))
            assertThat(name, equalTo(RecipeName("TestRecipe")))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(inputs, equalTo(RecipeInputs(HotObject.builder().set("hamal", "createdInputs").build())))
            assertThat(description, equalTo(RecipeDescription.empty))
        }
    }


    @Test
    fun `Tries to update Recipe that does not exist`() {
        val updateResponse = httpTemplate.patch("/v1/recipes/333333")
            .body(
                ApiRecipeUpdateRequest(
                    name = RecipeName("TestRecipe"),
                    value = CodeValue("40 + 2"),
                    inputs = RecipeInputs()
                )
            )
            .execute()

        assertThat(updateResponse.statusCode, equalTo(HttpStatusCode.NotFound))
        require(updateResponse is HttpErrorResponse) { "request was successful" }

        val error = updateResponse.error(ApiError::class)
        assertThat(error.message, equalTo("Recipe not found"))
    }
}