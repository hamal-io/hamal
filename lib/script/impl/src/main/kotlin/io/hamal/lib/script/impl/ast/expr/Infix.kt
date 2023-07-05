package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.Operator.*
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token

private val infixParseFnMapping = mapOf(
    IndexBasedAccess to TableAccessExpression.Parse,
    KeyBasedAccess to TableAccessExpression.Parse,
    Call to CallExpression.Parse,

    And to InfixExpression.Parse,
    Concat to InfixExpression.Parse,
    Equals to InfixExpression.Parse,
    Exponential to InfixExpression.Parse,
    Divide to InfixExpression.Parse,
    GreaterThan to InfixExpression.Parse,
    GreaterThanEquals to InfixExpression.Parse,
    LessThan to InfixExpression.Parse,
    LessThanEquals to InfixExpression.Parse,
    Minus to InfixExpression.Parse,
    Modulo to InfixExpression.Parse,
    Multiply to InfixExpression.Parse,
    NotEqual to InfixExpression.Parse,
    Or to InfixExpression.Parse,
    Plus to InfixExpression.Parse,
    ShiftLeft to InfixExpression.Parse,
    ShiftRight to InfixExpression.Parse,
)

internal fun infixFn(type: Token.Type): ParseInfixExpression? =
    infixParseFnMapping[Operator.from(type)]

internal interface ParseInfixExpression {
    operator fun invoke(ctx: Context, lhs: Expression): Expression
}


data class InfixExpression(
    override val position: Position,
    val lhs: Expression,
    val operator: Operator,
    var rhs: Expression
) : Expression {
    internal object Parse : ParseInfixExpression {
        override fun invoke(ctx: Context, lhs: Expression): Expression {
            val position = ctx.currentPosition()
            val precedence = ctx.currentPrecedence()
            val operator = Parse(ctx)
            val rhs = ctx.parseExpression(precedence)

            if (operator.rightAssociative() && lhs is InfixExpression) {
                return injectRightAssociativeExpression(ctx, lhs, operator, rhs)
            }
            return InfixExpression(position = position, lhs = lhs, operator = operator, rhs)
        }

        private fun Operator.rightAssociative(): Boolean = this == Exponential || this == Concat

        private fun injectRightAssociativeExpression(
            ctx: Context,
            lhs: InfixExpression,
            operator: Operator,
            rhs: Expression
        ): InfixExpression {
            val position = ctx.currentPosition()
            var nodeToInject: InfixExpression = lhs
            while (nodeToInject.rhs is InfixExpression) {
                nodeToInject = nodeToInject.rhs as InfixExpression
            }
            val newRhs = InfixExpression(
                position = position,
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

