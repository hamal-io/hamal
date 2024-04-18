package io.hamal.core.request.handler.recipe

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.RecipeCreateRequested
import io.hamal.lib.domain.vo.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class RecipeCreateHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Creates recipe`() {
        testInstance(submitCreateRecipeReq)

        with(recipeQueryRepository.get(RecipeId(123))) {
            assertThat(id, equalTo(RecipeId(123)))
            assertThat(name, equalTo(RecipeName("TestRecipe")))
            assertThat(value, equalTo(CodeValue("1 + 1")))
            assertThat(creatorId, equalTo(testAccount.id))
            assertThat(description, equalTo(RecipeDescription("TestDescription")))
        }
    }

    private val submitCreateRecipeReq by lazy {
        RecipeCreateRequested(
            requestId = RequestId(1),
            requestedBy = AuthId(2),
            requestStatus = RequestStatus.Submitted,
            id = RecipeId(123),
            name = RecipeName("TestRecipe"),
            inputs = RecipeInputs(HotObject.builder().set("hamal", "rocks").build()),
            value = CodeValue("1 + 1"),
            creatorId = testAccount.id,
            description = RecipeDescription("TestDescription")

        )
    }

    @Autowired
    private lateinit var testInstance: RecipeCreateHandler
}