package io.hamal.api.http.controller.recipe

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.RecipeDescription
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName.Companion.RecipeName
import io.hamal.lib.sdk.api.ApiRecipeCreateRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class RecipeCreateControllerTest : RecipeBaseControllerTest() {

    @Test
    fun `Creates Recipe`() {
        val res = createRecipe(
            ApiRecipeCreateRequest(
                name = RecipeName("TestRecipe"),
                inputs = RecipeInputs(ValueObject.builder().set("hamal", "rocks").build()),
                value = ValueCode("13 + 37")
            )
        )

        awaitCompleted(res)

        with(recipeQueryRepository.get(res.id)) {
            assertThat(inputs, equalTo(RecipeInputs(ValueObject.builder().set("hamal", "rocks").build())))
            assertThat(name, equalTo(RecipeName("TestRecipe")))
            assertThat(value, equalTo(ValueCode("13 + 37")))
            assertThat(description, equalTo(RecipeDescription.empty))
        }
    }
}