package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.Operator.*
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token

private val infixParseFnMapping = mapOf(
    Operator.And to InfixExpression.Parse,
    Concat to InfixExpression.Parse,
    Operator.Equals to InfixExpression.Parse,
    Exponential to InfixExpression.Parse,
    Operator.Divide to InfixExpression.Parse,
    Operator.Group to CallExpression.Parse,
    Operator.GreaterThan to InfixExpression.Parse,
    Operator.GreaterThanEquals to InfixExpression.Parse,
    Operator.LessThan to InfixExpression.Parse,
    Operator.LessThanEquals to InfixExpression.Parse,
    Operator.Minus to InfixExpression.Parse,
    Operator.Modulo to InfixExpression.Parse,
    Operator.Multiply to InfixExpression.Parse,
    Operator.NotEqual to InfixExpression.Parse,
    Operator.Or to InfixExpression.Parse,
    Operator.Plus to InfixExpression.Parse,
    Operator.ShiftLeft to InfixExpression.Parse,
    Operator.ShiftRight to InfixExpression.Parse,
)

internal fun infixFn(type: Token.Type): ParseInfixExpression? =
    infixParseFnMapping[Operator.from(type)]

internal interface ParseInfixExpression {
    operator fun invoke(ctx: Context, lhs: Expression): Expression
}


data class InfixExpression(
    val lhs: Expression,
    val operator: Operator,
    var rhs: Expression
) : Expression {
    internal object Parse : ParseInfixExpression {
        override fun invoke(ctx: Context, lhs: Expression): Expression {
            val precedence = ctx.currentPrecedence()
            val operator = Parse(ctx)
            val rhs = ctx.parseExpression(precedence)

            if (operator.rightAssociative() && lhs is InfixExpression) {
                return injectRightAssociativeExpression(lhs, operator, rhs)
            }
            return InfixExpression(lhs = lhs, operator = operator, rhs)
        }

        private fun Operator.rightAssociative(): Boolean = this == Exponential || this == Concat

        private fun injectRightAssociativeExpression(
            lhs: InfixExpression,
            operator: Operator,
            rhs: Expression
        ): InfixExpression {
            var nodeToInject: InfixExpression = lhs
            while (nodeToInject.rhs is InfixExpression) {
                nodeToInject = nodeToInject.rhs as InfixExpression
            }
            val newRhs = InfixExpression(
                lhs = nodeToInject.rhs,
                operator = operator,
                rhs = rhs
            )
            nodeToInject.rhs = newRhs
            return lhs
        }

    }

    override fun toString(): String {
        return "$lhs $operator $rhs"
    }
}

