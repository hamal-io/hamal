package io.hamal.core.request.handler.recipe

import io.hamal.core.request.handler.BaseRequestHandlerTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.RecipeUpdateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.RecipeCmdRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


internal class RecipeUpdateHandlerTest : BaseRequestHandlerTest() {

    @Test
    fun `Updates Recipe`() {
        recipeCmdRepository.create(
            RecipeCmdRepository.CreateCmd(
                id = CmdId(1),
                recipeId = RecipeId(123),
                name = RecipeName("TestRecipe"),
                inputs = RecipeInputs(HotObject.builder().set("hamal", "rocks").build()),
                value = CodeValue("1 + 1"),
                creatorId = testAccount.id,
                description = RecipeDescription("TestDescription")
            )
        )

        testInstance(submittedUpdateRecipeReq)

        with(recipeQueryRepository.get(RecipeId(123))) {
            assertThat(id, equalTo(RecipeId(123)))
            assertThat(name, equalTo(RecipeName("UpdatedRecipe")))
            assertThat(value, equalTo(CodeValue("40 + 2")))
            assertThat(inputs, equalTo(RecipeInputs(HotObject.builder().set("hamal", "updates").build())))
            assertThat(description, equalTo(RecipeDescription("TestUpdateDescription")))
        }
    }

    private val submittedUpdateRecipeReq by lazy {
        RecipeUpdateRequested(
            requestId = RequestId(2),
            requestedBy = AuthId(3),
            requestStatus = RequestStatus.Submitted,
            id = RecipeId(123),
            name = RecipeName("UpdatedRecipe"),
            inputs = RecipeInputs(HotObject.builder().set("hamal", "updates").build()),
            value = CodeValue("40 + 2"),
            description = RecipeDescription("TestUpdateDescription")
        )
    }

    @Autowired
    private lateinit var testInstance: RecipeUpdateHandler
}