package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token

internal interface ParsePrefixExpression<EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context): EXPRESSION
}

private val prefixParseFnMapping = mapOf(
    Token.Type.True to True.Parse,
    Token.Type.False to False.Parse,
    Token.Type.Nil to Nil.Parse,
    Token.Type.String to String.Parse,
    Token.Type.Identifier to Identifier.Parse,
    Token.Type.Number to Number.Parse
)

internal fun prefixFn(type: Token.Type) = prefixParseFnMapping[type]!!