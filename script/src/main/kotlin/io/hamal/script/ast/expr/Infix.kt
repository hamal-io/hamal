package io.hamal.script.ast.expr

import io.hamal.script.ast.Parser
import io.hamal.script.ast.expr.Operator.Parse
import io.hamal.script.ast.parseExpression
import io.hamal.script.token.Token.Type
import io.hamal.script.token.Token.Type.*

private val infixParseFnMapping = mapOf(
    Plus to InfixExpression.Parse,
    Minus to InfixExpression.Parse,
    LeftParenthesis to CallExpression.Parse,
)

internal interface ParseInfixExpression {
    operator fun invoke(ctx: Parser.Context, lhs: Expression): Expression
}


data class InfixExpression(
    val lhs: Expression,
    val operator: Operator,
    val rhs: Expression
) : Expression {
    internal object Parse : ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): Expression {
            val precedence = ctx.currentPrecedence()
            val operator = Parse(ctx)
            val rhs = ctx.parseExpression(precedence)
            return InfixExpression(
                lhs = lhs, operator = operator, rhs = rhs
            )
        }
    }
}

internal fun infixFn(type: Type): ParseInfixExpression? = infixParseFnMapping[type]

