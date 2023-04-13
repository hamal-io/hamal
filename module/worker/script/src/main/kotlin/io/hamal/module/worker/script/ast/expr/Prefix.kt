package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token

internal interface ParsePrefixExpression {
    operator fun invoke(ctx: Parser.Context): Expression
}

private val prefixParseFnMapping = mapOf(
    Token.Type.TrueLiteral to True.Parse,
    Token.Type.FalseLiteral to False.Parse,
    Token.Type.NilLiteral to Nil.ParseNilLiteral,
    Token.Type.StringLiteral to String.Parse,
    Token.Type.Identifier to Identifier.Parse,
    Token.Type.NumberLiteral to Number.Parse
)

internal fun prefixFn(type: Token.Type) = prefixParseFnMapping[type]!!