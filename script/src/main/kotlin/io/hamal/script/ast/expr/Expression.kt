package io.hamal.script.ast.expr

import io.hamal.script.ast.Node
import io.hamal.script.ast.Parser
import io.hamal.script.token.Token

interface Expression : Node
internal interface ParseExpression<EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context): EXPRESSION
}

private val parseFnMapping = mapOf(
    Token.Type.True to TrueLiteral.Parse,
    Token.Type.False to FalseLiteral.Parse,
    Token.Type.Nil to NilLiteral.Parse,
    Token.Type.String to StringLiteral.Parse,
    Token.Type.Identifier to Identifier.Parse,
    Token.Type.Number to NumberLiteral.Parse,
    Token.Type.Function to PrototypeLiteral.Parse,
    Token.Type.Minus to PrefixExpression.Parse,
    Token.Type.LeftParenthesis to GroupedExpression.Parse
)

internal fun parseFn(type: Token.Type) : ParseExpression<*> {
    return parseFnMapping[type]!!
}


