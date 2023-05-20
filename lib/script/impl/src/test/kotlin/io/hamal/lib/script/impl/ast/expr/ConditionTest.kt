package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.stmt.BlockStatement
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
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))),
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42))))
            )
        }

        @Test
        fun `Not Equal if condition or block statement are not equal`() {
            assertNotEquals(
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))),
                ConditionalExpression(FalseLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42))))
            )

            assertNotEquals(
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))),
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(StringLiteral("Something else"))))
            )
        }

    }


    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if condition and block statement hashcode are same`() {
            assertEquals(
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode()
            )
        }

        @Test
        fun `Different hashcode if condition or block statement has different hashcode`() {
            assertNotEquals(
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalExpression(FalseLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode()
            )

            assertNotEquals(
                ConditionalExpression(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalExpression(
                    TrueLiteral,
                    BlockStatement(ExpressionStatement(StringLiteral("Something else")))
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
                    BlockStatement(ExpressionStatement(NumberLiteral(13)))
                )
            )
        ),
        "if false then 13 else 26 end" to IfExpression(
            listOf(
                ConditionalExpression(
                    FalseLiteral,
                    BlockStatement(ExpressionStatement(NumberLiteral(13)))
                ),
                ConditionalExpression(
                    TrueLiteral,
                    BlockStatement(ExpressionStatement(NumberLiteral(26)))
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