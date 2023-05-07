package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.*
import io.hamal.lib.script.impl.ast.expr.*

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
        appendExpression(expression.expression)
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
            appendString(it.toString())
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
            is StringLiteral -> appendValue(StringValue(literal.value))
            is NilLiteral -> appendValue(NilValue)
        }
    }

    private fun StringBuilder.appendValue(value: Value) {
        append(value.toString())
    }
}

