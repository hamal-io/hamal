package io.hamal.lib.script.impl.ast.expr

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class IndexFieldExpressionTest {
    @Nested
    @DisplayName("constructor()")
    inner class ConstructorTest {

        @Test
        fun ok() {
            IndexFieldExpression(1, TrueLiteral)
            IndexFieldExpression(10, TrueLiteral)
            IndexFieldExpression(100, TrueLiteral)
            IndexFieldExpression(1000000, TrueLiteral)
        }

        @Test
        fun `Index can not be 0`() {
            val exception = assertThrows<IllegalArgumentException> {
                IndexFieldExpression(0, TrueLiteral)
            }
            assertThat(exception.message, equalTo("First element starts with index 1"))
        }

        @Test
        fun `Index can not be negative`() {
            val exception = assertThrows<IllegalArgumentException> {
                IndexFieldExpression(-42, TrueLiteral)
            }
            assertThat(exception.message, equalTo("First element starts with index 1"))
        }
    }

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if index is equal`() {
            assertEquals(
                IndexFieldExpression(1, TrueLiteral),
                IndexFieldExpression(1, TrueLiteral),
            )

            assertEquals(
                IndexFieldExpression(1, TrueLiteral),
                IndexFieldExpression(1, FalseLiteral),
            )
        }

        @Test
        fun `Not Equal if index is not equal`() {
            assertNotEquals(
                IndexFieldExpression(1, TrueLiteral),
                IndexFieldExpression(2, TrueLiteral),
            )

            assertNotEquals(
                IndexFieldExpression(1, TrueLiteral),
                IndexFieldExpression(2, FalseLiteral),
            )
        }

    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if index has same hashcode`() {
            assertEquals(
                IndexFieldExpression(1, TrueLiteral).hashCode(),
                IndexFieldExpression(1, TrueLiteral).hashCode(),
            )

            assertEquals(
                IndexFieldExpression(1, TrueLiteral).hashCode(),
                IndexFieldExpression(1, FalseLiteral).hashCode(),
            )
        }

        @Test
        fun `Different hashcode if index has different hashcode`() {
            assertNotEquals(
                IndexFieldExpression(1, TrueLiteral).hashCode(),
                IndexFieldExpression(2, TrueLiteral).hashCode(),
            )

            assertNotEquals(
                IndexFieldExpression(1, TrueLiteral).hashCode(),
                IndexFieldExpression(2, FalseLiteral).hashCode(),
            )
        }
    }
}

internal class KeyFieldExpressionTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if key is equal`() {
            assertEquals(
                KeyFieldExpression("1", TrueLiteral),
                KeyFieldExpression("1", TrueLiteral),
            )

            assertEquals(
                KeyFieldExpression("1", TrueLiteral),
                KeyFieldExpression("1", FalseLiteral),
            )
        }

        @Test
        fun `Not Equal if key is not equal`() {
            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral),
                KeyFieldExpression("2", TrueLiteral),
            )

            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral),
                KeyFieldExpression("2", FalseLiteral),
            )
        }

    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if key has same hashcode`() {
            assertEquals(
                KeyFieldExpression("1", TrueLiteral).hashCode(),
                KeyFieldExpression("1", TrueLiteral).hashCode(),
            )

            assertEquals(
                KeyFieldExpression("1", TrueLiteral).hashCode(),
                KeyFieldExpression("1", FalseLiteral).hashCode(),
            )
        }

        @Test
        fun `Different hashcode if key has different hashcode`() {
            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral).hashCode(),
                KeyFieldExpression("2", TrueLiteral).hashCode(),
            )

            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral).hashCode(),
                KeyFieldExpression("2", FalseLiteral).hashCode(),
            )
        }
    }
}

internal class TableConstructorTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("Parse()")
    inner class Parse {
        @Test
        fun `Parse empty table constructor`() {
            runTest(
                TableConstructor.Parse,
                """
                    {}
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.fieldExpressions, empty())
                tokens.wereConsumed()
            }
        }

        @Nested
        inner class Array {
            @Test
            fun `Parse array with single number`() {
                runTest(
                    TableConstructor.Parse,
                    """
                    {1}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(result.fieldExpressions, equalTo(listOf(IndexFieldExpression(1, NumberLiteral(1)))))
                    tokens.wereConsumed()
                }
            }

            @Test
            fun `Parse array with multiple numbers`() {
                runTest(
                    TableConstructor.Parse,
                    """
                    {3,2,1}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions, equalTo(
                            listOf(
                                IndexFieldExpression(1, NumberLiteral(3)),
                                IndexFieldExpression(2, NumberLiteral(2)),
                                IndexFieldExpression(3, NumberLiteral(1)),
                            )
                        )
                    )
                    tokens.wereConsumed()
                }
            }

            @Test
            fun `Parse array consisting of number, boolean and string`() {
                runTest(
                    TableConstructor.Parse,
                    """
                    {'hamal', 1337, true}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions, equalTo(
                            listOf(
                                IndexFieldExpression(1, StringLiteral("hamal")),
                                IndexFieldExpression(2, NumberLiteral(1337)),
                                IndexFieldExpression(3, TrueLiteral),
                            )
                        )
                    )
                    tokens.wereConsumed()
                }
            }
        }

        @Nested
        inner class Map {
            @Test
            fun `Parse map with single key-value pair`() {
                runTest(
                    TableConstructor.Parse,
                    """
                    {key = 'value'}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions, equalTo(
                            listOf(
                                KeyFieldExpression("key", StringLiteral("value"))
                            )
                        )
                    )
                    tokens.wereConsumed()
                }
            }

            @Test
            fun `Parse map with multiple key-value pairs`() {
                runTest(
                    TableConstructor.Parse,
                    """
                    {
                        key_one = 'value_one',
                        key_two = 'value_two'
                    }
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions, equalTo(
                            listOf(
                                KeyFieldExpression("key_one", StringLiteral("value_one")),
                                KeyFieldExpression("key_two", StringLiteral("value_two"))
                            )
                        )
                    )
                    tokens.wereConsumed()
                }
            }

        }

        @Test
        fun `Parse map mixed constructor`() {
            runTest(
                TableConstructor.Parse,
                """
                    {
                        'value-1',
                        key_one = 'value_one',
                        'value-2',
                        key_two = 'value_two'
                        'value-3'
                    }
                """.trimIndent()
            ) { result, tokens ->
                assertThat(
                    result.fieldExpressions, equalTo(
                        listOf(
                            IndexFieldExpression(1, StringLiteral("value-1")),
                            KeyFieldExpression("key_one", StringLiteral("value_one")),
                            IndexFieldExpression(2, StringLiteral("value-2")),
                            KeyFieldExpression("key_two", StringLiteral("value_two")),
                            IndexFieldExpression(3, StringLiteral("value-3"))
                        )
                    )
                )
                tokens.wereConsumed()
            }
        }

    }
}
