package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.ParsePrefixExpression
import io.hamal.module.worker.script.ast.Visitor
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type

class Identifier(val value: String) : Expression {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseIdentifier : ParsePrefixExpression<Identifier> {
        override fun invoke(tokens: ArrayDeque<Token>): Identifier {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            assert(token.type == Type.Identifier)
            return Identifier(token.value.value)
        }
    }
}