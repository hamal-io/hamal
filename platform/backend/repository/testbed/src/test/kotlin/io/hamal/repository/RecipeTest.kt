package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.AccountId.Companion.AccountId
import io.hamal.lib.domain.vo.RecipeDescription
import io.hamal.lib.domain.vo.RecipeDescription.Companion.RecipeDescription
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.domain.vo.RecipeId.Companion.RecipeId
import io.hamal.lib.domain.vo.RecipeInputs
import io.hamal.lib.domain.vo.RecipeName
import io.hamal.lib.domain.vo.RecipeName.Companion.RecipeName
import io.hamal.repository.api.RecipeCmdRepository.CreateCmd
import io.hamal.repository.api.RecipeCmdRepository.UpdateCmd
import io.hamal.repository.api.RecipeQueryRepository.RecipeQuery
import io.hamal.repository.api.RecipeRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

class RecipeRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreatesRecipeTest {

        @TestFactory
        fun `Creates Recipe`() = runWith(RecipeRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    recipeId = RecipeId(123),
                    creatorId = AccountId("123"),
                    name = RecipeName("TestRecipe"),
                    inputs = RecipeInputs(HotObject.builder().set("hamal", "rocks").build()),
                    value = ValueCode("1 + 1"),
                    description = RecipeDescription("Nice Recipe")
                )
            )

            with(result) {
                assertThat(id, equalTo(RecipeId(123)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(RecipeName("TestRecipe")))
                assertThat(inputs, equalTo(RecipeInputs(HotObject.builder().set("hamal", "rocks").build())))
                assertThat(value, equalTo(ValueCode("1 + 1")))
                assertThat(description, equalTo(RecipeDescription("Nice Recipe")))
            }
            verifyCount(1)
        }

        @TestFactory
        fun `Creates Recipe duplicate`() = runWith(RecipeRepository::class) {
            createRecipe(
                recipeId = RecipeId(1),
                name = RecipeName("TestRecipe"),
                value = ValueCode("40 + 2"),
            )

            createRecipe(
                recipeId = RecipeId(2),
                name = RecipeName("TestRecipe"),
                value = ValueCode("40 + 2")
            )

            verifyCount(2)

            with(get(RecipeId(1))) {
                assertThat(id, equalTo(RecipeId(1)))
                assertThat(value, equalTo(ValueCode("40 + 2")))
                assertThat(name, equalTo(RecipeName("TestRecipe")))
                assertThat(creatorId, equalTo(AccountId("123")))
            }

            with(get(RecipeId(2))) {
                assertThat(id, equalTo(RecipeId(2)))
                assertThat(value, equalTo(ValueCode("40 + 2")))
                assertThat(name, equalTo(RecipeName("TestRecipe")))
                assertThat(creatorId, equalTo(AccountId("123")))
            }
        }
    }

    @Nested
    inner class UpdatesRecipeTest {
        @TestFactory
        fun `Updates Recipe`() = runWith(RecipeRepository::class) {
            createRecipe(
                recipeId = RecipeId(1),
                name = RecipeName("TestRecipe")
            )

            val result = update(
                RecipeId(1),
                UpdateCmd(
                    id = CmdId(2),
                    name = RecipeName("TestRecipe2"),
                    value = ValueCode("1 + 1"),
                    inputs = RecipeInputs(HotObject.builder().set("answer", 42).build()),
                    description = RecipeDescription("Updated description")
                )
            )

            with(result) {
                assertThat(id, equalTo(RecipeId(1)))
                assertThat(name, equalTo(RecipeName("TestRecipe2")))
                assertThat(value, equalTo(ValueCode("1 + 1")))
                assertThat(inputs, equalTo(RecipeInputs(HotObject.builder().set("answer", 42).build())))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(description, equalTo(RecipeDescription("Updated description")))

            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates Recipe multiple times`() = runWith(RecipeRepository::class) {
            createRecipe(
                recipeId = RecipeId(1),
                name = RecipeName("TestRecipe")
            )

            repeat(13) { iteration ->
                val result = update(
                    RecipeId(1), UpdateCmd(
                        id = CmdId(iteration + 2),
                        name = RecipeName("TestRecipe$iteration"),
                        value = ValueCode("40 + $iteration"),
                        description = RecipeDescription("Updated description + $iteration")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(RecipeId(1)))
                    assertThat(creatorId, equalTo(AccountId("123")))
                    assertThat(name, equalTo(RecipeName("TestRecipe$iteration")))
                    assertThat(value, equalTo(ValueCode("40 + $iteration")))
                    assertThat(description, equalTo(RecipeDescription("Updated description + $iteration")))
                }
            }
            verifyCount(1)
        }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get Recipe by id`() = runWith(RecipeRepository::class) {
            createRecipe(
                recipeId = RecipeId(1),
                name = RecipeName("TestRecipe"),
                value = ValueCode("1 + 1")
            )

            with(find(RecipeId(1))!!) {
                assertThat(id, equalTo(RecipeId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(RecipeName("TestRecipe")))
                assertThat(value, equalTo(ValueCode("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to get Recipe by id but does not exist`() = runWith(RecipeRepository::class) {
            val exception = assertThrows<NoSuchElementException> {
                get(RecipeId(111111))
            }
            assertThat(exception.message, equalTo("Recipe not found"))
        }
    }

    @Nested
    inner class FindTest {

        @TestFactory
        fun `Find Recipe by id`() = runWith(RecipeRepository::class) {
            createRecipe(
                recipeId = RecipeId(1),
                name = RecipeName("TestRecipe"),
                value = ValueCode("1 + 1")
            )

            with(find(RecipeId(1))!!) {
                assertThat(id, equalTo(RecipeId(1)))
                assertThat(creatorId, equalTo(AccountId("123")))
                assertThat(name, equalTo(RecipeName("TestRecipe")))
                assertThat(value, equalTo(ValueCode("1 + 1")))
            }
        }

        @TestFactory
        fun `Tries to find Recipe by id but does not exist`() = runWith(RecipeRepository::class) {
            assertThat(find(RecipeId(111111)), nullValue())
        }
    }

    @Nested
    inner class ClearTest {
        @TestFactory
        fun `Nothing to clear`() = runWith(RecipeRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(RecipeRepository::class) {
            setup()
            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class ListAndCountTest {
        @TestFactory
        fun `By ids`() = runWith(RecipeRepository::class) {
            setup()

            val result = list(listOf(RecipeId(111111), RecipeId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(RecipeId(3)))
                assertThat(name, equalTo(RecipeName("Recipe")))
            }

        }


        @TestFactory
        fun `Limit`() = runWith(RecipeRepository::class) {
            setup()

            val query = RecipeQuery(
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(RecipeRepository::class) {
            setup()

            val query = RecipeQuery(
                afterId = RecipeId(2),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(RecipeId(1)))
            }
        }
    }

    private fun RecipeRepository.setup() {
        createRecipe(
            recipeId = RecipeId(1),
            name = RecipeName("Recipe")
        )

        createRecipe(
            recipeId = RecipeId(2),
            name = RecipeName("Recipe")
        )

        createRecipe(
            recipeId = RecipeId(3),
            name = RecipeName("Recipe")
        )

        createRecipe(
            recipeId = RecipeId(4),
            name = RecipeName("Recipe")
        )
    }

    private fun RecipeRepository.createRecipe(
        recipeId: RecipeId,
        name: RecipeName,
        value: ValueCode = ValueCode("1 + 1"),
        cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
    ) {
        create(
            CreateCmd(
                id = cmdId,
                recipeId = recipeId,
                creatorId = AccountId("123"),
                name = name,
                inputs = RecipeInputs(HotObject.builder().set("hamal", "rocks").build()),
                value = value,
                description = RecipeDescription.empty
            )
        )
    }

    private fun RecipeRepository.verifyCount(expected: Int) {
        verifyCount(expected) {}
    }

    private fun RecipeRepository.verifyCount(expected: Int, block: RecipeQuery.() -> Unit) {
        val counted = count(RecipeQuery().also(block))
        assertThat("number of Recipes expected", counted, equalTo(Count(expected)))
    }
}