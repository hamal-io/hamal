package io.hamal.script.ast.expr

import io.hamal.script.ast.Expression
import io.hamal.script.ast.Parser
import io.hamal.script.token.Token

internal interface ParsePrefixExpression<EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context): EXPRESSION
}

private val prefixParseFnMapping = mapOf(
    Token.Type.True to True.Parse,
    Token.Type.False to False.Parse,
    Token.Type.Nil to Nil.Parse,
    Token.Type.String to String.Parse,
    Token.Type.Identifier to Identifier.Parse,
    Token.Type.Number to Number.Parse,
    Token.Type.Function to Function.Parse,
    Token.Type.LeftParenthesis to GroupedExpression.Parse
)

internal fun prefixFn(type: Token.Type) : ParsePrefixExpression<*> {
    return prefixParseFnMapping[type]!!
}


