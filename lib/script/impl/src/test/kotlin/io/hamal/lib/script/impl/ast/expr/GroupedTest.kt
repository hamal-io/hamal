package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.somePosition
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GroupedExpressionTest : AbstractExpressionTest() {
    @Nested
    inner class ParseTest {
        @Test
        fun `empty grouped expression`() {
            runTest(GroupedExpression.Parse, "( )") { result, tokens ->
                assertThat(result, equalTo(GroupedExpression(somePosition, NilLiteral(somePosition))))
                tokens.consumed()
            }
        }

        @Test
        fun `literal grouped expression`() {
            runTest(GroupedExpression.Parse, "(2810)") { result, tokens ->
                assertThat(result, equalTo(GroupedExpression(somePosition, NumberLiteral(somePosition, 2810))))
                tokens.consumed()
            }
        }

        @Test
        fun `prefix expression`() {
            runTest(GroupedExpression.Parse, "(-23)") { result, tokens ->
                assertThat(
                    result, equalTo(
                        GroupedExpression(
                            somePosition,
                            PrefixExpression(somePosition, Operator.Minus, NumberLiteral(somePosition, 23))
                        )
                    )
                )
                tokens.consumed()
            }
        }

        @Test
        fun `infix expression`() {
            runTest(GroupedExpression.Parse, "(1+2)") { result, tokens ->
                assertThat(
                    result, equalTo(
                        GroupedExpression(
                            somePosition,
                            InfixExpression(
                                somePosition,
                                NumberLiteral(somePosition, 1),
                                Operator.Plus,
                                NumberLiteral(somePosition, 2)
                            )
                        )
                    )
                )
                tokens.consumed()
            }
        }
    }
}