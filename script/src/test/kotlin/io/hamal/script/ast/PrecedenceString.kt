package io.hamal.script.ast

import io.hamal.script.ast.expr.GroupedExpression
import io.hamal.script.ast.expr.Identifier
import io.hamal.script.ast.expr.Number
import io.hamal.script.ast.expr.Operator
import io.hamal.script.value.*

object PrecedenceString {
    fun of(expression: Expression): String {
        val builder = StringBuilder()
        builder.appendExpression(expression)
        return builder.toString()
    }

    private fun StringBuilder.appendExpression(expression: Expression) {
        when (expression) {
            is Identifier -> appendString(expression.value)
            is io.hamal.script.ast.expr.LiteralExpression -> appendLiteral(expression)
            is PrefixExpression -> appendPrefixExpression(expression)
            is InfixExpression -> appendInfixExpression(expression)
            is GroupedExpression -> appendGroupedExpression(expression)
            else -> TODO("$expression")
        }
    }

    private fun StringBuilder.appendPrefixExpression(expression: PrefixExpression) {
        append('(')
        appendOperator(expression.operator)
        appendExpression(expression)
        append(')')
    }

    private fun StringBuilder.appendGroupedExpression(expression: GroupedExpression) {
        append('(')
        appendExpression(expression.expression)
        append(')')
    }

    private fun StringBuilder.appendInfixExpression(expression: InfixExpression) {
        append('(')
        appendExpression(expression.lhs)
        append(' ')
        appendOperator(expression.operator)
        append(' ')
        appendExpression(expression.rhs)
        append(')')
    }

    private fun StringBuilder.appendOperator(operator: Operator?) {
        operator?.let {
            appendString(it.value)
        }
    }

    private fun StringBuilder.appendString(value: String) {
        append(value)
    }

    private fun StringBuilder.appendLiteral(literal: io.hamal.script.ast.expr.LiteralExpression) {
        when (literal) {
            is io.hamal.script.ast.expr.True -> appendValue(TrueValue)
            is io.hamal.script.ast.expr.False -> appendValue(FalseValue)
            is Number -> appendValue(NumberValue(literal.value))
            is io.hamal.script.ast.expr.String -> appendValue(StringValue(literal.value))
            is io.hamal.script.ast.expr.Nil -> appendValue(NilValue)
        }
    }

    private fun StringBuilder.appendValue(value: Value) {
        append(value.toString())
    }
}

