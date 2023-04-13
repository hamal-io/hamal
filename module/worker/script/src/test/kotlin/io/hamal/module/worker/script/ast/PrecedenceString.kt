package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.*
import io.hamal.module.worker.script.ast.expr.Number
import io.hamal.module.worker.script.value.*

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
        }
    }

    private fun StringBuilder.appendPrefixExpression(expression: PrefixExpression) {
        append('(')
        appendOperator(expression.operator)
        appendExpression(expression)
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
            is True -> appendValue(TrueValue)
            is False -> appendValue(FalseValue)
            is Number -> appendValue(NumberValue(literal.value))
            is io.hamal.module.worker.script.ast.expr.String -> appendValue(StringValue(literal.value))
            is Nil -> appendValue(NilValue)
        }
    }

    private fun StringBuilder.appendValue(value: Value) {
        append(value.toString())
    }
}

