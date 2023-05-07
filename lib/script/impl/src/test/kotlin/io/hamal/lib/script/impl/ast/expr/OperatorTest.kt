package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.PrecedenceString
import io.hamal.lib.script.impl.ast.AbstractAstTest
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.Parser.DefaultImpl.parse
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import io.hamal.lib.script.impl.token.tokenize
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
                Arguments.of("a < b", "(a < b)"),
                Arguments.of("a <= b", "(a <= b)"),
                Arguments.of("a > b", "(a > b)"),
                Arguments.of("a..b", "(a .. b)"),
                Arguments.of("a >= b", "(a >= b)"),
                Arguments.of("a == b", "(a == b)"),
                Arguments.of("a ~= b", "(a ~= b)"),
                Arguments.of("(a + b)", "((a + b))"),
                Arguments.of("a + b + c", "((a + b) + c)"),
                Arguments.of("1 << 2", "(1 << 2)"),
                Arguments.of("2 >> 1", "(2 >> 1)"),
                Arguments.of("a and b or c and d", "((a and b) or (c and d))"),
                Arguments.of("b/2+1", "((b / 2) + 1)"),
                Arguments.of("b*2-1", "((b * 2) - 1)"),
                Arguments.of("2^8", "(2 ^ 8)"),
                Arguments.of("a+i < b/2+1", "((a + i) < ((b / 2) + 1))"),
                Arguments.of("5+x^2*8", "(5 + ((x ^ 2) * 8))"),
                Arguments.of("a < y and y <= z ", "((a < y) and (y <= z))"),
                Arguments.of("-x^2", "(-(x ^ 2))"),
                Arguments.of("x^y^z", "(x ^ (y ^ z))"),
                Arguments.of("w^x^y^z", "(w ^ (x ^ (y ^ z)))"),
                Arguments.of("x..y..z", "(x .. (y .. z))")
            )
        }

        @JvmStatic
        private fun operatorParsingTestCases(): List<Arguments> {
            return listOf(
                Arguments.of("and", Operator.And),
                Arguments.of("..", Operator.Concat),
                Arguments.of("/", Operator.Divide),
                Arguments.of("==", Operator.Equals),
                Arguments.of("^", Operator.Exponential),
                Arguments.of(">", Operator.GreaterThan),
                Arguments.of(">=", Operator.GreaterThanEquals),
                Arguments.of("(", Operator.Group),
                Arguments.of("#", Operator.Length),
                Arguments.of("<", Operator.LessThan),
                Arguments.of("<=", Operator.LessThanEquals),
                Arguments.of("-", Operator.Minus),
                Arguments.of("*", Operator.Multiply),
                Arguments.of("not", Operator.Not),
                Arguments.of("~=", Operator.NotEqual),
                Arguments.of("or", Operator.Or),
                Arguments.of("+", Operator.Plus),
                Arguments.of("<<", Operator.ShiftLeft),
                Arguments.of(">>", Operator.ShiftRight)
            )
        }
    }

    @ParameterizedTest(name = "#{index} - Precedence of {0}")
    @MethodSource("precedenceTestCases")
    fun `Operator precedence`(given: String, expected: String) {
        val tokens = tokenize(given)

        val blockStatement = parse(Context(ArrayDeque(tokens)))
        assertThat(blockStatement.statements, hasSize(1))
        val statement = blockStatement.statements.first()
        require(statement is ExpressionStatement)

        val result = PrecedenceString.of(statement.expression)
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest(name = "#{index} - Parse {0}")
    @MethodSource("operatorParsingTestCases")
    fun `Operator parsing`(given: String, expected: Operator) {
        val tokens = ArrayDeque(tokenize(given))
        val result = Operator.Parse(Context(tokens))
        assertThat(result, equalTo(expected));
        tokens.wereConsumed()
    }
}