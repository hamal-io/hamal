package io.hamal.script.ast.expr

import io.hamal.script.ast.AbstractAstTest
import io.hamal.script.ast.ExpressionStatement
import io.hamal.script.ast.Parser
import io.hamal.script.PrecedenceString
import io.hamal.script.token.tokenize
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class OperatorTest : AbstractAstTest() {

    companion object {
        @JvmStatic
        private fun precedenceTestCases(): List<Arguments> {
            return listOf(
                Arguments.of("a + b", "(a + b)"),
                Arguments.of("a - b", "(a - b)"),
                Arguments.of("(a + b)", "((a + b))"),
                Arguments.of("a + b + c", "((a + b) + c)"),
            );
        }

        @JvmStatic
        private fun operatorParsingTestCases(): List<Arguments> {
            return listOf(
                Arguments.of("+", Operator.Plus),
                Arguments.of("-", Operator.Minus),
            )
        }
    }

    @ParameterizedTest(name = "#{index} - Precedence of {0}")
    @MethodSource("precedenceTestCases")
    fun `Operator precedence`(given: kotlin.String, expected: kotlin.String) {
        val tokens = tokenize(given)

        val blockStatement = Parser.DefaultImpl.parse(Parser.Context(ArrayDeque(tokens)))
        assertThat(blockStatement.statements, hasSize(1))
        val statement = blockStatement.statements.first()
        require(statement is ExpressionStatement)

        val result = PrecedenceString.of(statement.expression)
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest(name = "#{index} - Parse {0}")
    @MethodSource("operatorParsingTestCases")
    fun `Operator parsing`(given: kotlin.String, expected: Operator) {
        val tokens = ArrayDeque(tokenize(given))
        val result = Operator.Parse(Parser.Context(tokens))
        assertThat(result, equalTo(expected));
        tokens.wereConsumed()
    }
}