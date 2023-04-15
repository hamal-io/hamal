package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.InfixExpression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.expr.Operator.Parse
import io.hamal.module.worker.script.ast.parseExpression
import io.hamal.module.worker.script.token.Token.Type
import io.hamal.module.worker.script.token.Token.Type.*

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

