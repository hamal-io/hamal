package io.hamal.script.ast.expr

import io.hamal.script.token.Token.Type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GroupedTest : AbstractExpressionTest() {
    @Nested
    @DisplayName("Parse")
    inner class ParseTest {
        @Test
        fun `empty grouped expression`() {
            runTest(GroupedExpression.Parse, "( )") { result, tokens ->
                assertThat(result, equalTo(GroupedExpression(NilLiteral())))
                tokens.inOrder(Type.RightParenthesis, Type.Eof)
            }
        }

        @Test
        fun `literal grouped expression`() {
            runTest(GroupedExpression.Parse, "(2810)") { result, tokens ->
                assertThat(result, equalTo(GroupedExpression(NumberLiteral(2810))))
                tokens.inOrder(Type.RightParenthesis, Type.Eof)
            }
        }

        @Test
        fun `prefix expression`() {
            runTest(GroupedExpression.Parse, "(-23)") { result, tokens ->
                assertThat(
                    result, equalTo(
                        GroupedExpression(
                            PrefixExpression(Operator.Minus, NumberLiteral(23))
                        )
                    )
                )
                tokens.inOrder(Type.RightParenthesis, Type.Eof)
            }
        }

        @Test
        fun `infix expression`() {
            runTest(GroupedExpression.Parse, "(1+2)") { result, tokens ->
                assertThat(
                    result, equalTo(
                        GroupedExpression(
                            InfixExpression(
                                NumberLiteral(1),
                                Operator.Plus,
                                NumberLiteral(2)
                            )
                        )
                    )
                )
                tokens.inOrder(Type.RightParenthesis, Type.Eof)
            }
        }
    }
}