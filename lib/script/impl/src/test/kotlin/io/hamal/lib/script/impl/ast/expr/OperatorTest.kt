package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.PrecedenceString
import io.hamal.lib.script.impl.ast.AbstractAstTest
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.Parser.DefaultImpl.parse
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import io.hamal.lib.script.impl.token.tokenize
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class OperatorTest : AbstractAstTest() {
    @TestFactory
    fun `Operator precedence`() = listOf(
        Pair("a + b", "(a + b)"),
        Pair("a - b", "(a - b)"),
        Pair("a < b", "(a < b)"),
        Pair("a <= b", "(a <= b)"),
        Pair("a > b", "(a > b)"),
        Pair("a..b", "(a .. b)"),
        Pair("a >= b", "(a >= b)"),
        Pair("a == b", "(a == b)"),
        Pair("a ~= b", "(a ~= b)"),
        Pair("(a + b)", "((a + b))"),
        Pair("a + b + c", "((a + b) + c)"),
        Pair("1 << 2", "(1 << 2)"),
        Pair("2 >> 1", "(2 >> 1)"),
        Pair("a and b or c and d", "((a and b) or (c and d))"),
        Pair("b/2+1", "((b / 2) + 1)"),
        Pair("b*2-1", "((b * 2) - 1)"),
        Pair("2^8", "(2 ^ 8)"),
        Pair("a+i < b/2+1", "((a + i) < ((b / 2) + 1))"),
        Pair("5+x^2*8", "(5 + ((x ^ 2) * 8))"),
        Pair("a < y and y <= z ", "((a < y) and (y <= z))"),
        Pair("-x^2", "(-(x ^ 2))"),
        Pair("x^y^z", "(x ^ (y ^ z))"),
        Pair("w^x^y^z", "(w ^ (x ^ (y ^ z)))"),
        Pair("x..y..z", "(x .. (y .. z))")
    ).map { (code, expected) ->
        DynamicTest.dynamicTest(code) {
            val tokens = tokenize(code)

            val blockStatement = parse(Context(ArrayDeque(tokens)))
            assertThat(blockStatement.statements, hasSize(1))
            val statement = blockStatement.statements.first()
            require(statement is ExpressionStatement)

            val result = PrecedenceString.of(statement.expression)
            assertThat(result, equalTo(expected));
        }
    }

    @TestFactory
    fun `Operator parsing`() = listOf(
        Pair("and", Operator.And),
        Pair("..", Operator.Concat),
        Pair("/", Operator.Divide),
        Pair("==", Operator.Equals),
        Pair("^", Operator.Exponential),
        Pair(">", Operator.GreaterThan),
        Pair(">=", Operator.GreaterThanEquals),
        Pair("(", Operator.Group),
        Pair("#", Operator.Length),
        Pair("<", Operator.LessThan),
        Pair("<=", Operator.LessThanEquals),
        Pair("-", Operator.Minus),
        Pair("*", Operator.Multiply),
        Pair("not", Operator.Not),
        Pair("~=", Operator.NotEqual),
        Pair("or", Operator.Or),
        Pair("+", Operator.Plus),
        Pair("<<", Operator.ShiftLeft),
        Pair(">>", Operator.ShiftRight)
    ).map { (code, expected) ->
        DynamicTest.dynamicTest(code) {
            val tokens = ArrayDeque(tokenize(code))
            val result = Operator.Parse(Context(tokens))
            assertThat(result, equalTo(expected));
            tokens.wereConsumed()
        }
    }

}