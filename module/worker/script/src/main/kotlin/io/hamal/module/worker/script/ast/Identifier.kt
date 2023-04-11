package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token.Identifier

class Identifier(
    override val token: Identifier
) : Node {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}