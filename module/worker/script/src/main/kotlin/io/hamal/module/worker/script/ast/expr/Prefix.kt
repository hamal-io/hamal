package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token

internal interface ParsePrefixExpression {
    operator fun invoke(ctx: Parser.Context): Expression
}

private val prefixParseFnMapping = mapOf(
    Token.Type.TrueLiteral to TrueLiteral.ParseTrueLiteral,
    Token.Type.FalseLiteral to FalseLiteral.ParseFalseLiteral,
    Token.Type.NilLiteral to NilLiteral.ParseNilLiteral,
    Token.Type.StringLiteral to StringLiteral.ParseStringLiteral,
    Token.Type.Identifier to Identifier.ParseIdentifier,
    Token.Type.NumberLiteral to NumberLiteral.ParseNumberLiteral
)

internal fun Parser.Context.prefixFn(type: Token.Type) = prefixParseFnMapping[type]!!