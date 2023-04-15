package io.hamal.script.ast.expr

import io.hamal.script.ast.Expression
import io.hamal.script.ast.Parser
import io.hamal.script.ast.parseExpression
import io.hamal.script.token.Token.Type

internal interface ParseExpression<EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context): EXPRESSION
}

data class PrefixExpression(
    val operator: Operator,
    val value: Expression
) : Expression {
    internal object Parse : ParseExpression<PrefixExpression>{
        override fun invoke(ctx: Parser.Context): PrefixExpression {
            return PrefixExpression(
                Operator.Parse(ctx),
                ctx.parseExpression(Precedence.Prefix)
            )
        }
    }
}


private val parseFnMapping = mapOf(
    Type.True to True.Parse,
    Type.False to False.Parse,
    Type.Nil to Nil.Parse,
    Type.String to String.Parse,
    Type.Identifier to Identifier.Parse,
    Type.Number to Number.Parse,
    Type.Function to Prototype.Parse,
    Type.Minus to PrefixExpression.Parse,
    Type.LeftParenthesis to GroupedExpression.Parse
)

internal fun parseFn(type: Type) : ParseExpression<*> {
    return parseFnMapping[type]!!
}


