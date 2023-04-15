package io.hamal.script.ast.expr

import io.hamal.script.ast.Expression
import io.hamal.script.ast.InfixExpression
import io.hamal.script.ast.Parser
import io.hamal.script.ast.expr.Operator.Parse
import io.hamal.script.ast.parseExpression
import io.hamal.script.token.Token.Type
import io.hamal.script.token.Token.Type.*

private val infixParseFnMapping = mapOf(
    Plus to ParseInfixExpression.DefaultImpl,
    Minus to ParseInfixExpression.DefaultImpl,
    LeftParenthesis to CallExpression.Parse,
)

internal interface ParseInfixExpression {
    operator fun invoke(ctx: Parser.Context, lhs: Expression): Expression

    object DefaultImpl : ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): InfixExpression {
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

