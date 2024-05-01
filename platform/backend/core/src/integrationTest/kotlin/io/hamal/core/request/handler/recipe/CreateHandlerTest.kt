package io.hamal.core.request.handler.recipe

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.RecipeCreateRequested
import io.hamal.lib.domain.vo.AuthId.Companion.AuthId
import io.hamal.lib.domain.vo.RecipeDescription.Companion.RecipeDescription
import io.hamal.lib.domain.vo.RecipeId.Companion.RecipeId
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName.Companion.RecipeName
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId
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
            assertThat(value, equalTo(ValueCode("1 + 1")))
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
            value = ValueCode("1 + 1"),
            creatorId = testAccount.id,
            description = RecipeDescription("TestDescription")

        )
    }

    @Autowired
    private lateinit var testInstance: RecipeCreateHandler
}