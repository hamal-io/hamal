package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.token.Token.Type
import io.hamal.lib.script.impl.token.Token.Type.*
import io.hamal.lib.script.impl.token.Token.Type.Function
import io.hamal.lib.script.impl.token.Token.Type.Number


internal interface ParseExpression<EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context): EXPRESSION
}

private val tokenMapping = mapOf(
    True to TrueLiteral.Parse,
    False to FalseLiteral.Parse,
    Nil to NilLiteral.Parse,
    Code to CodeLiteral.Parse,
    Type.String to StringLiteral.Parse,
    Identifier to IdentifierLiteral.Parse,
    Number to NumberLiteral.Parse,
    Function to PrototypeLiteral.Parse,
    If to IfExpression.Parse,
    For to ForLoopExpression.Parse
)

private val operatorMapping = mapOf(
    Operator.Minus to PrefixExpression.Parse,
    Operator.Call to GroupedExpression.Parse,
    Operator.TableConstructor to TableConstructorExpression.Parse
)

internal fun parseFn(type: Type): ParseExpression<*> {
    return when (type.category) {
        Category.Operator -> operatorMapping[Operator.from(type)]!!
        else -> tokenMapping[type]!!
    }
}

