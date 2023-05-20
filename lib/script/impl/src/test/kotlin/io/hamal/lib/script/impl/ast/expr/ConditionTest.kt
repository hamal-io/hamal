package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.stmt.Assignment
import io.hamal.lib.script.impl.ast.stmt.BlockStatement
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ConditionalStatementTest {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if condition and block statement are equal`() {
            assertEquals(
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))),
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42))))
            )
        }

        @Test
        fun `Not Equal if condition or block statement are not equal`() {
            assertNotEquals(
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))),
                ConditionalStatement(FalseLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42))))
            )

            assertNotEquals(
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))),
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(StringLiteral("Something else"))))
            )
        }

    }


    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if condition and block statement hashcode are same`() {
            assertEquals(
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode()
            )
        }

        @Test
        fun `Different hashcode if condition or block statement has different hashcode`() {
            assertNotEquals(
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalStatement(FalseLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode()
            )

            assertNotEquals(
                ConditionalStatement(TrueLiteral, BlockStatement(ExpressionStatement(NumberLiteral(42)))).hashCode(),
                ConditionalStatement(
                    TrueLiteral,
                    BlockStatement(ExpressionStatement(StringLiteral("Something else")))
                ).hashCode()
            )
        }

    }


}

internal class IfExpressionTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        @DisplayName("if a<0 then a = 0 end")
        fun `Simple if expression `() {
            runTest(IfExpression.Parse, """if a<0 then a = 0 end""") { result, tokens ->
                assertThat(
                    result, equalTo(
                        IfExpression(
                            listOf(
                                ConditionalStatement(
                                    InfixExpression(IdentifierLiteral("a"), Operator.LessThan, NumberLiteral(0)),
                                    BlockStatement(Assignment.Global(IdentifierLiteral("a"), NumberLiteral(0)))
                                )
                            )
                        )
                    )
                )
                tokens.wereConsumed()
            }
        }

    }

}