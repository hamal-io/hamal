package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.value.StringValue

class Identifier(val value: StringValue) : Node {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object Parser : NodeParser<Identifier> {
        override fun invoke(tokens: ArrayDeque<Token>): Identifier {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            require(token is Token.Identifier)
            return Identifier(StringValue(token.value.value))
        }
    }
}