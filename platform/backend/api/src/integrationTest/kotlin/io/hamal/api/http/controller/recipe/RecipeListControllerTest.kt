package io.hamal.api.http.controller.recipe

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.RecipeDescription
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName
import io.hamal.lib.sdk.api.ApiRecipeCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

internal class RecipeListControllerTest : RecipeBaseControllerTest() {

    @Test
    fun `List Recipes`() {
        repeat(10) { iteration ->
            awaitCompleted(
                createRecipe(
                    ApiRecipeCreateRequest(
                        name = RecipeName("TestRecipe $iteration"),
                        value = CodeValue("40 + 2"),
                        inputs = RecipeInputs(),
                        description = RecipeDescription("TestRecipeDescription")

                    )
                )
            )
        }


        with(listRecipe()) {
            assertThat(recipes, hasSize(10))
            with(recipes.last()) {
                assertThat(name, equalTo(RecipeName("TestRecipe 0")))
                assertThat(description, equalTo(RecipeDescription("TestRecipeDescription")))
            }
        }
    }
}