package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.stmt.Block
import io.hamal.lib.script.impl.ast.stmt.DoStmt
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ConditionalExpressionTest {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if condition and block statement are equal`() {
            assertEquals(
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42)))),
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42))))
            )
        }

        @Test
        fun `Not Equal if condition or block statement are not equal`() {
            assertNotEquals(
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42)))),
                ConditionalExpression(FalseLiteral, Block(ExpressionStatement(NumberLiteral(42))))
            )

            assertNotEquals(
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42)))),
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(StringLiteral("Something else"))))
            )
        }

    }


    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if condition and block statement hashcode are same`() {
            assertEquals(
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42)))).hashCode()
            )
        }

        @Test
        fun `Different hashcode if condition or block statement has different hashcode`() {
            assertNotEquals(
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalExpression(FalseLiteral, Block(ExpressionStatement(NumberLiteral(42)))).hashCode()
            )

            assertNotEquals(
                ConditionalExpression(TrueLiteral, Block(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalExpression(
                    TrueLiteral,
                    Block(ExpressionStatement(StringLiteral("Something else")))
                ).hashCode()
            )
        }
    }
}

internal class IfExpressionTest : AbstractExpressionTest() {
    @TestFactory
    fun parse() = listOf(
        "if true then 13 end" to IfExpression(
            listOf(
                ConditionalExpression(
                    TrueLiteral,
                    Block(ExpressionStatement(NumberLiteral(13)))
                )
            )
        ),
        "if false then 13 else 26 end" to IfExpression(
            listOf(
                ConditionalExpression(
                    FalseLiteral,
                    Block(ExpressionStatement(NumberLiteral(13)))
                ),
                ConditionalExpression(
                    TrueLiteral,
                    Block(ExpressionStatement(NumberLiteral(26)))
                )
            )
        ),
        "if false then 13 elseif true then 23 else 26 end" to IfExpression(
            listOf(
                ConditionalExpression(
                    FalseLiteral,
                    Block(ExpressionStatement(NumberLiteral(13)))
                ),
                ConditionalExpression(
                    TrueLiteral,
                    Block(ExpressionStatement(NumberLiteral(23)))
                ),
                ConditionalExpression(
                    TrueLiteral,
                    Block(ExpressionStatement(NumberLiteral(26)))
                )
            )
        )
    ).map { (code, expected) ->
        dynamicTest(code) {
            runTest(IfExpression.Parse, code) { result, tokens ->
                assertThat(result, equalTo(expected))
                tokens.wereConsumed()
            }
        }
    }
}


internal class ForExpressionTest : AbstractExpressionTest() {
    @TestFactory
    fun parse() = listOf(
        "for i=1,10 do true end" to ForLoopExpression(
            identifier = IdentifierLiteral("i"),
            startExpression = NumberLiteral(1),
            endExpression = NumberLiteral(10),
            stepExpression = NumberLiteral(1),
            block = DoStmt(Block(ExpressionStatement(TrueLiteral)))
        ),
        "for i=1,10,2 do true end" to ForLoopExpression(
            identifier = IdentifierLiteral("i"),
            startExpression = NumberLiteral(1),
            endExpression = NumberLiteral(10),
            stepExpression = NumberLiteral(2),
            block = DoStmt(Block(ExpressionStatement(TrueLiteral)))
        ),
    ).map { (code, expected) ->
        dynamicTest(code) {
            runTest(ForLoopExpression.Parse, code) { result, tokens ->
                assertThat(result, equalTo(expected))
                tokens.wereConsumed()
            }
        }
    }
}