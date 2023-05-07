package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.Token.Type.Category

interface Expression : io.hamal.lib.script.impl.ast.Node
internal interface ParseExpression<EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context): EXPRESSION
}

private val tokenMapping = mapOf(
    Token.Type.True to TrueLiteral.Parse,
    Token.Type.False to FalseLiteral.Parse,
    Token.Type.Nil to NilLiteral.Parse,
    Token.Type.String to StringLiteral.Parse,
    Token.Type.Identifier to Identifier.Parse,
    Token.Type.Number to NumberLiteral.Parse,
    Token.Type.Function to PrototypeLiteral.Parse,
)

private val operatorMapping = mapOf(
    Operator.Minus to PrefixExpression.Parse,
    Operator.Group to GroupedExpression.Parse
)

internal fun parseFn(type: Token.Type): ParseExpression<*> {
    return when (type.category) {
        Category.Operator -> operatorMapping[Operator.from(type)]!!
        else -> tokenMapping[type]!!
    }
}

