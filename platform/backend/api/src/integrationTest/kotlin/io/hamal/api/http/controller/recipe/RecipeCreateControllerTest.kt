package io.hamal.api.http.controller.recipe

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.RecipeDescription
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName
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
                inputs = RecipeInputs(HotObject.builder().set("hamal", "rocks").build()),
                value = CodeValue("13 + 37")
            )
        )

        awaitCompleted(res)

        with(recipeQueryRepository.get(res.id)) {
            assertThat(inputs, equalTo(RecipeInputs(HotObject.builder().set("hamal", "rocks").build())))
            assertThat(name, equalTo(RecipeName("TestRecipe")))
            assertThat(value, equalTo(CodeValue("13 + 37")))
            assertThat(description, equalTo(RecipeDescription.empty))
        }
    }
}