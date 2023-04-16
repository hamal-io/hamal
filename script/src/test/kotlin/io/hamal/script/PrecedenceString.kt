package io.hamal.script

import io.hamal.script.ast.expr.*
import io.hamal.script.ast.expr.NumberLiteral
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
            is LiteralExpression -> appendLiteral(expression)
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

    private fun StringBuilder.appendLiteral(literal: LiteralExpression) {
        when (literal) {
            is TrueLiteral -> appendValue(TrueValue)
            is FalseLiteral -> appendValue(FalseValue)
            is NumberLiteral -> appendValue(NumberValue(literal.value))
            is io.hamal.script.ast.expr.StringLiteral -> appendValue(StringValue(literal.value))
            is NilLiteral -> appendValue(NilValue)
        }
    }

    private fun StringBuilder.appendValue(value: Value) {
        append(value.toString())
    }
}

