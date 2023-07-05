package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.anotherPosition
import io.hamal.lib.script.impl.ast.stmt.Block
import io.hamal.lib.script.impl.ast.stmt.DoStmt
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import io.hamal.lib.script.impl.somePosition
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ConditionalExpressionTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if condition and block statement are equal`() {
            assertEquals(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 42)))
                ),
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 42)))
                )
            )

            assertEquals(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 42)))
                ),
                ConditionalExpression(
                    anotherPosition,
                    TrueLiteral(anotherPosition),
                    Block(anotherPosition, ExpressionStatement(anotherPosition, NumberLiteral(anotherPosition, 42)))
                )
            )
        }

        @Test
        fun `Not Equal if condition or block statement are not equal`() {
            assertNotEquals(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 42)))
                ),
                ConditionalExpression(
                    somePosition,
                    FalseLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 42)))
                )
            )

            assertNotEquals(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition), Block(
                        somePosition, ExpressionStatement(
                            somePosition, NumberLiteral(
                                somePosition, 42
                            )
                        )
                    )
                ),
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition), Block(
                        somePosition, ExpressionStatement(
                            somePosition, StringLiteral(
                                somePosition, "Something else"
                            )
                        )
                    )
                )
            )
        }

    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if condition and block statement hashcode are same`() {
            assertEquals(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition), Block(
                        somePosition, ExpressionStatement(
                            somePosition, NumberLiteral(
                                somePosition, 42
                            )
                        )
                    )
                ).hashCode(),
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition), Block(
                        somePosition, ExpressionStatement(
                            somePosition, NumberLiteral(
                                somePosition, 42
                            )
                        )
                    )
                ).hashCode()
            )

            assertEquals(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition), Block(
                        somePosition, ExpressionStatement(
                            somePosition, NumberLiteral(
                                somePosition, 42
                            )
                        )
                    )
                ).hashCode(),
                ConditionalExpression(
                    anotherPosition,
                    TrueLiteral(anotherPosition), Block(
                        anotherPosition, ExpressionStatement(
                            anotherPosition, NumberLiteral(
                                anotherPosition, 42
                            )
                        )
                    )
                ).hashCode()
            )

        }

        @Test
        fun `Different hashcode if condition or block statement has different hashcode`() {
            assertNotEquals(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition), Block(
                        somePosition,
                        ExpressionStatement(
                            somePosition, NumberLiteral(
                                somePosition, 42
                            )
                        )
                    )
                ).hashCode(),
                ConditionalExpression(
                    somePosition, FalseLiteral(somePosition), Block(
                        somePosition, ExpressionStatement(
                            somePosition, NumberLiteral(somePosition, 42)
                        )
                    )
                ).hashCode()
            )

            assertNotEquals(
                ConditionalExpression(
                    somePosition, TrueLiteral(somePosition), Block(
                        somePosition, ExpressionStatement(
                            somePosition, NumberLiteral(somePosition, 42)
                        )
                    )
                ).hashCode(),
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(
                        somePosition,
                        ExpressionStatement(somePosition, StringLiteral(somePosition, "Something else"))
                    )
                ).hashCode()
            )
        }
    }
}

internal class IfExpressionTest : AbstractExpressionTest() {
    @TestFactory
    fun parse() = listOf(
        "if true then 13 end" to IfExpression(
            somePosition,
            listOf(
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 13)))
                )
            )
        ),
        "if false then 13 else 26 end" to IfExpression(
            somePosition,
            listOf(
                ConditionalExpression(
                    somePosition,
                    FalseLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 13)))
                ),
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 26)))
                )
            )
        ),
        "if false then 13 elseif true then 23 else 26 end" to IfExpression(
            somePosition,
            listOf(
                ConditionalExpression(
                    somePosition,
                    FalseLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 13)))
                ),
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 23)))
                ),
                ConditionalExpression(
                    somePosition,
                    TrueLiteral(somePosition),
                    Block(somePosition, ExpressionStatement(somePosition, NumberLiteral(somePosition, 26)))
                )
            )
        )
    ).map { (code, expected) ->
        dynamicTest(code) {
            runTest(IfExpression.Parse, code) { result, tokens ->
                assertThat(result, equalTo(expected))
                tokens.consumed()
            }
        }
    }
}


internal class ForExpressionTest : AbstractExpressionTest() {
    @TestFactory
    fun parse() = listOf(
        "for i=1,10 do true end" to ForLoopExpression(
            position = somePosition,
            ident = IdentifierLiteral(somePosition, "i"),
            startExpression = NumberLiteral(somePosition, 1),
            endExpression = NumberLiteral(somePosition, 10),
            stepExpression = NumberLiteral(somePosition, 1),
            block = DoStmt(
                somePosition,
                Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
            )
        ),
        "for i=1,10,2 do true end" to ForLoopExpression(
            position = somePosition,
            ident = IdentifierLiteral(somePosition, "i"),
            startExpression = NumberLiteral(somePosition, 1),
            endExpression = NumberLiteral(somePosition, 10),
            stepExpression = NumberLiteral(somePosition, 2),
            block = DoStmt(
                somePosition, Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
            ),
        )
    ).map { (code, expected) ->
        dynamicTest(code) {
            runTest(ForLoopExpression.Parse, code) { result, tokens ->
                assertThat(result, equalTo(expected))
                tokens.consumed()
            }
        }
    }
}