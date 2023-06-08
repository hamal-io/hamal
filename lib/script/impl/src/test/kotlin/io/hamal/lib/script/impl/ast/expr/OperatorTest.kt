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
        "a + b" to "(a + b)",
        "a - b" to "(a - b)",
        "a < b" to "(a < b)",
        "a <= b" to "(a <= b)",
        "a > b" to "(a > b)",
        "a..b" to "(a .. b)",
        "a >= b" to "(a >= b)",
        "a == b" to "(a == b)",
        "a ~= b" to "(a ~= b)",
        "(a + b)" to "((a + b))",
        "a + b + c" to "((a + b) + c)",
        "1 << 2" to "(1 << 2)",
        "2 >> 1" to "(2 >> 1)",
        "a and b or c and d" to "((a and b) or (c and d))",
        "b/2+1" to "((b / 2) + 1)",
        "b*2-1" to "((b * 2) - 1)",
        "2^8" to "(2 ^ 8)",
        "a+i < b/2+1" to "((a + i) < ((b / 2) + 1))",
        "5+x^2*8" to "(5 + ((x ^ 2) * 8))",
        "a < y and y <= z " to "((a < y) and (y <= z))",
        "-x^2" to "(-(x ^ 2))",
        "x^y^z" to "(x ^ (y ^ z))",
        "w^x^y^z" to "(w ^ (x ^ (y ^ z)))",
        "v^w^x^y^z" to "(v ^ (w ^ (x ^ (y ^ z))))",
        "x..y..z" to "(x .. (y .. z))",
        "w..x..y..z" to "(w .. (x .. (y .. z)))",
        "v..w..x..y..z" to "(v .. (w .. (x .. (y .. z))))"
    ).map { (code, expected) ->
        DynamicTest.dynamicTest(code) {
            val tokens = tokenize(code)

            val blockStatement = parse(Context(ArrayDeque(tokens)))
            assertThat(blockStatement.statements, hasSize(1))
            val statement = blockStatement.statements.first()
            require(statement is ExpressionStatement)

            val result = PrecedenceString.of(statement.expression)
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `Operator parsing`() = listOf(
        "and" to Operator.And,
        ".." to Operator.Concat,
        "/" to Operator.Divide,
        "==" to Operator.Equals,
        "^" to Operator.Exponential,
        ">" to Operator.GreaterThan,
        ">=" to Operator.GreaterThanEquals,
        "(" to Operator.Call,
        "#" to Operator.Length,
        "<" to Operator.LessThan,
        "<=" to Operator.LessThanEquals,
        "-" to Operator.Minus,
        "*" to Operator.Multiply,
        "not" to Operator.Not,
        "~=" to Operator.NotEqual,
        "or" to Operator.Or,
        "+" to Operator.Plus,
        "<<" to Operator.ShiftLeft,
        ">>" to Operator.ShiftRight
    ).map { (code, expected) ->
        DynamicTest.dynamicTest(code) {
            val tokens = ArrayDeque(tokenize(code))
            val result = Operator.Parse(Context(tokens))
            assertThat(result, equalTo(expected))
            tokens.consumed()
        }
    }

}