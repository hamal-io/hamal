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
            val precedence = ctx.currentPrecedence()
            val operator = ctx.parseOperator()

            val rhs = ctx.parseExpression(precedence)
            return InfixExpression(
                lhs = lhs,
                operator = operator,
                rhs = rhs
            )
        }
    }
}

internal fun infixFn(type: Token.Type): ParseInfixExpression? = infixParseFnMapping[type]

internal fun Parser.Context.parseOperator(): Operator {
    val result = Operator.Plus
    advance()
    return result
}