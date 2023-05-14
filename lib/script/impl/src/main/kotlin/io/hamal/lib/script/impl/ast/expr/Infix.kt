package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.Expression
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.Operator.Parse
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token

private val infixParseFnMapping = mapOf(
    Operator.And to InfixExpression.Parse,
    Operator.Concat to InfixExpression.Parse,
    Operator.Equals to InfixExpression.Parse,
    Operator.Exponential to InfixExpression.Parse,
    Operator.Divide to InfixExpression.Parse,
    Operator.Group to CallExpression.Parse,
    Operator.GreaterThan to InfixExpression.Parse,
    Operator.GreaterThanEquals to InfixExpression.Parse,
    Operator.LessThan to InfixExpression.Parse,
    Operator.LessThanEquals to InfixExpression.Parse,
    Operator.Minus to InfixExpression.Parse,
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
    val rhs: Expression
) : Expression {
    internal object Parse : ParseInfixExpression {
        override fun invoke(ctx: Context, lhs: Expression): Expression {
            val precedence = ctx.currentPrecedence()
            return when (val operator = Parse(ctx)) {
                Operator.Exponential, Operator.Concat -> ctx.parseRightAssociative(operator, lhs)
                else -> InfixExpression(lhs = lhs, operator = operator, rhs = ctx.parseExpression(precedence))
            }
        }

        private fun Context.parseRightAssociative(operator: Operator, lhs: Expression): Expression {
            val rhs = parseExpression(precedenceOf(operator))
            if (this.nextTokenType() == Token.Type.Eof) {
                return InfixExpression(lhs, operator, rhs)
            }
            if (nextPrecedence() < precedenceOf(operator)) {
                advance()
                return Parse(this, InfixExpression(lhs = lhs, operator = Operator.Exponential, rhs = rhs))
            }
            return InfixExpression(lhs, operator, parseExpression(currentPrecedence()))
        }
    }

    override fun toString(): String {
        return "$lhs $operator $rhs"
    }
}

