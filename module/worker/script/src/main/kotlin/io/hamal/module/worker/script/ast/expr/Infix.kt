package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.*
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type.Plus

private val infixParseFnMapping = mapOf(
    Plus to ParseInfixExpression.DefaultImpl
)

internal interface ParseInfixExpression {
    operator fun invoke(ctx: Parser.Context, lhs: Expression): InfixExpression

    object DefaultImpl : ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): InfixExpression {
            val operator = Operator(ctx.currentTokenType().value)
            val precedence = ctx.currentPrecedence()
            ctx.advance()
            val rhs = ctx.parseExpression(precedence)
            return InfixExpression(
                lhs = lhs,
                operator = operator,
                rhs = rhs
            )
        }
    }
}

internal fun Parser.Context.infixFn(type: Token.Type): ParseInfixExpression? = infixParseFnMapping[type]