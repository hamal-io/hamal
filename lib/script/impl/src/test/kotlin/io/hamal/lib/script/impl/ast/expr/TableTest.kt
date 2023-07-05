package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.anotherPosition
import io.hamal.lib.script.impl.somePosition
import io.hamal.lib.script.impl.token.Token.Type.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class TableIndexLiteralTest {
    @Nested
    inner class ConstructorTest {

        @Test
        fun ok() {
            IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition))
            IndexFieldExpression(TableIndexLiteral(somePosition, 10), TrueLiteral(somePosition))
            IndexFieldExpression(TableIndexLiteral(somePosition, 100), TrueLiteral(somePosition))
            IndexFieldExpression(TableIndexLiteral(somePosition, 1000000), TrueLiteral(somePosition))
        }

        @Test
        fun `Index can not be 0`() {
            val exception = assertThrows<IllegalArgumentException> {
                IndexFieldExpression(TableIndexLiteral(somePosition, 0), TrueLiteral(somePosition))
            }
            assertThat(exception.message, equalTo("First element starts with index 1"))
        }

        @Test
        fun `Index can not be negative`() {
            val exception = assertThrows<IllegalArgumentException> {
                IndexFieldExpression(TableIndexLiteral(somePosition, -42), TrueLiteral(somePosition))
            }
            assertThat(exception.message, equalTo("First element starts with index 1"))
        }
    }

}

internal class IndexFieldExpressionTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if index is equal`() {
            assertEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)),
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)),
            )

            assertEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)),
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), FalseLiteral(somePosition)),
            )

            assertEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)),
                IndexFieldExpression(TableIndexLiteral(anotherPosition, 1), TrueLiteral(anotherPosition)),
            )
        }

        @Test
        fun `Not Equal if index is not equal`() {
            assertNotEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)),
                IndexFieldExpression(TableIndexLiteral(somePosition, 2), TrueLiteral(somePosition)),
            )

            assertNotEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)),
                IndexFieldExpression(TableIndexLiteral(somePosition, 2), FalseLiteral(somePosition)),
            )
        }

    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if index has same hashcode`() {
            assertEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)).hashCode(),
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)).hashCode(),
            )

            assertEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)).hashCode(),
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), FalseLiteral(somePosition)).hashCode(),
            )

            assertEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)).hashCode(),
                IndexFieldExpression(TableIndexLiteral(anotherPosition, 1), FalseLiteral(anotherPosition)).hashCode(),
            )

        }

        @Test
        fun `Different hashcode if index has different hashcode`() {
            assertNotEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)).hashCode(),
                IndexFieldExpression(TableIndexLiteral(somePosition, 2), TrueLiteral(somePosition)).hashCode(),
            )

            assertNotEquals(
                IndexFieldExpression(TableIndexLiteral(somePosition, 1), TrueLiteral(somePosition)).hashCode(),
                IndexFieldExpression(TableIndexLiteral(somePosition, 2), FalseLiteral(somePosition)).hashCode(),
            )
        }
    }
}

internal class KeyFieldExpressionTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if key is equal`() {
            assertEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)),
                KeyFieldExpression("1", TrueLiteral(somePosition)),
            )

            assertEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)),
                KeyFieldExpression("1", FalseLiteral(somePosition)),
            )

            assertEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)),
                KeyFieldExpression("1", TrueLiteral(anotherPosition)),
            )

        }

        @Test
        fun `Not Equal if key is not equal`() {
            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)),
                KeyFieldExpression("2", TrueLiteral(somePosition)),
            )

            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)),
                KeyFieldExpression("2", FalseLiteral(somePosition)),
            )
        }

    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if key has same hashcode`() {
            assertEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)).hashCode(),
                KeyFieldExpression("1", TrueLiteral(somePosition)).hashCode(),
            )

            assertEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)).hashCode(),
                KeyFieldExpression("1", FalseLiteral(somePosition)).hashCode(),
            )

            assertEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)).hashCode(),
                KeyFieldExpression("1", TrueLiteral(anotherPosition)).hashCode(),
            )
        }

        @Test
        fun `Different hashcode if key has different hashcode`() {
            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)).hashCode(),
                KeyFieldExpression("2", TrueLiteral(somePosition)).hashCode(),
            )

            assertNotEquals(
                KeyFieldExpression("1", TrueLiteral(somePosition)).hashCode(),
                KeyFieldExpression("2", FalseLiteral(somePosition)).hashCode(),
            )
        }
    }
}

internal class TableConstructorExpressionTest : AbstractExpressionTest() {
    @Nested
    inner class Parse {
        @Test
        fun `Parse empty table constructor`() {
            runTest(
                TableConstructorExpression.Parse,
                """
                    {}
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.fieldExpressions, empty())
                tokens.consumed()
            }
        }

        @Nested
        inner class Array {
            @Test
            fun `Parse array with single number`() {
                runTest(
                    TableConstructorExpression.Parse,
                    """
                    {1}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions,
                        equalTo(
                            listOf(
                                IndexFieldExpression(
                                    TableIndexLiteral(somePosition, 1), NumberLiteral(
                                        somePosition, 1
                                    )
                                )
                            )
                        )
                    )
                    tokens.consumed()
                }
            }

            @Test
            fun `Parse array with multiple numbers`() {
                runTest(
                    TableConstructorExpression.Parse,
                    """
                    {3,2,1}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions, equalTo(
                            listOf(
                                IndexFieldExpression(
                                    TableIndexLiteral(somePosition, 1),
                                    NumberLiteral(somePosition, 3)
                                ),
                                IndexFieldExpression(
                                    TableIndexLiteral(somePosition, 2),
                                    NumberLiteral(somePosition, 2)
                                ),
                                IndexFieldExpression(
                                    TableIndexLiteral(somePosition, 3),
                                    NumberLiteral(somePosition, 1)
                                ),
                            )
                        )
                    )
                    tokens.consumed()
                }
            }

            @Test
            fun `Parse array consisting of number, boolean and string`() {
                runTest(
                    TableConstructorExpression.Parse,
                    """
                    {'hamal', 1337, true}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions, equalTo(
                            listOf(
                                IndexFieldExpression(
                                    TableIndexLiteral(somePosition, 1),
                                    StringLiteral(somePosition, "hamal")
                                ),
                                IndexFieldExpression(
                                    TableIndexLiteral(somePosition, 2),
                                    NumberLiteral(somePosition, 1337)
                                ),
                                IndexFieldExpression(TableIndexLiteral(somePosition, 3), TrueLiteral(somePosition)),
                            )
                        )
                    )
                    tokens.consumed()
                }
            }
        }

        @Nested
        inner class Map {
            @Test
            fun `Parse map with single key-value pair`() {
                runTest(
                    TableConstructorExpression.Parse,
                    """
                    {key = 'value'}
                """.trimIndent()
                ) { result, tokens ->
                    assertThat(
                        result.fieldExpressions, equalTo(
                            listOf(
                                KeyFieldExpression("key", StringLiteral(somePosition, "value"))
                            )
                        )
                    )
                    tokens.consumed()
                }
            }

            @Test
            fun `Parse map with multiple key-value pairs`() {
                runTest(
                    TableConstructorExpression.Parse,
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
                                KeyFieldExpression("key_one", StringLiteral(somePosition, "value_one")),
                                KeyFieldExpression("key_two", StringLiteral(somePosition, "value_two"))
                            )
                        )
                    )
                    tokens.consumed()
                }
            }

        }

        @Test
        fun `Parse map mixed constructor`() {
            runTest(
                TableConstructorExpression.Parse,
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
                            IndexFieldExpression(
                                TableIndexLiteral(somePosition, 1),
                                StringLiteral(somePosition, "value-1")
                            ),
                            KeyFieldExpression("key_one", StringLiteral(somePosition, "value_one")),
                            IndexFieldExpression(
                                TableIndexLiteral(somePosition, 2),
                                StringLiteral(somePosition, "value-2")
                            ),
                            KeyFieldExpression("key_two", StringLiteral(somePosition, "value_two")),
                            IndexFieldExpression(
                                TableIndexLiteral(somePosition, 3),
                                StringLiteral(somePosition, "value-3")
                            )
                        )
                    )
                )
                tokens.consumed()
            }
        }

    }
}

internal class TableAccessExpressionTest : AbstractExpressionTest() {
    @Test
    fun `Parse table access by index`() {
        runInfixTest(
            TableAccessExpression.Parse,
            IdentifierLiteral(somePosition, "table"),
            """[1]"""
        ) { result, tokens ->
            require(result is TableAccessExpression)
            assertThat(result.target, equalTo(IdentifierLiteral(somePosition, "table")))
            assertThat(result.key, equalTo(TableIndexLiteral(somePosition, 1)))
            tokens.consumed()
        }
    }

    @Test
    fun `Parse table access by multi digit index`() {
        runInfixTest(
            TableAccessExpression.Parse,
            IdentifierLiteral(somePosition, "table"),
            """[123456789]"""
        ) { result, tokens ->
            require(result is TableAccessExpression)
            assertThat(result.target, equalTo(IdentifierLiteral(somePosition, "table")))
            assertThat(result.key, equalTo(TableIndexLiteral(somePosition, 123456789)))
            tokens.consumed()
        }
    }

    @Test
    fun `Parse table access by ident`() {
        runInfixTest(
            TableAccessExpression.Parse,
            IdentifierLiteral(somePosition, "table"),
            """.some_field"""
        ) { result, tokens ->
            require(result is TableAccessExpression)
            assertThat(result.target, equalTo(IdentifierLiteral(somePosition, "table")))
            assertThat(result.key, equalTo(TableKeyLiteral(somePosition, "some_field")))
            tokens.consumed()
        }
    }

    @Test
    fun `Parse table access by string`() {
        runInfixTest(
            TableAccessExpression.Parse,
            IdentifierLiteral(somePosition, "table"),
            """['some_field']"""
        ) { result, tokens ->
            require(result is TableAccessExpression)
            assertThat(result.target, equalTo(IdentifierLiteral(somePosition, "table")))
            assertThat(result.key, equalTo(TableKeyLiteral(somePosition, "some_field")))
            tokens.consumed()
        }
    }

    @Test
    fun `Parse table nested table access`() {
        runInfixTest(
            TableAccessExpression.Parse,
            IdentifierLiteral(somePosition, "table"),
            """.some_field.another_field"""
        ) { result, tokens ->
            require(result is TableAccessExpression)
            assertThat(result.target, equalTo(IdentifierLiteral(somePosition, "table")))
            assertThat(result.key, equalTo(TableKeyLiteral(somePosition, "some_field")))
            tokens.inOrder(Dot, Ident, Eof)
        }
    }

}