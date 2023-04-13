package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.tokenize
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class OperatorPrecedenceTest : AbstractAstTest() {

    @ParameterizedTest(name = "#{index} - Precedence of {0}")
    @MethodSource("testCases")
    fun `Parameterized tests`(given: String, expected: String) {
        val tokens = tokenize(given)

        val statements = Parser.DefaultImpl.parse(Parser.Context(ArrayDeque(tokens)))
        assertThat(statements, hasSize(1))
        val statement = statements.first()
        require(statement is StatementExpression)

        val result = PrecedenceString.of(statement.expression)
        assertThat(result, equalTo(expected));
    }

    companion object {
        @JvmStatic
        private fun testCases(): List<Arguments> {
            return listOf(
                Arguments.of("a + b", "(a + b)"),
                Arguments.of("a + b + c", "((a + b) + c)"),
            );
        }
    }
}