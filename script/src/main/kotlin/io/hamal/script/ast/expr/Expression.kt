package io.hamal.script.ast.expr

import io.hamal.script.ast.Node
import io.hamal.script.ast.Parser
import io.hamal.script.token.Token
import io.hamal.script.token.Token.Type.Category

interface Expression : Node
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

