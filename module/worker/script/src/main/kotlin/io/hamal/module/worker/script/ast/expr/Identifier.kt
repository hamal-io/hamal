package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.ParsePrefixExpression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Visitor
import io.hamal.module.worker.script.token.Token.Type

class Identifier(val value: String) : Expression {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseIdentifier : ParsePrefixExpression<Identifier> {
        override fun invoke(ctx: Parser.Context): Identifier {
            assert(ctx.isNotEmpty())
            val token = ctx.pop()
            assert(token.type == Type.Identifier)
            return Identifier(token.value.value)
        }
    }
}