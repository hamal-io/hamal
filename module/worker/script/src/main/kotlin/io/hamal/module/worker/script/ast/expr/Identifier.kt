package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token.Type

class Identifier(val value: String) : Expression {
    internal object ParseIdentifier : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): Identifier {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Identifier)
            return Identifier(token.value)
        }
    }
}