package io.hamal.module.worker.script.ast.literal

import io.hamal.module.worker.script.ast.ExpressionLiteral
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Visitor
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Literal

class LiteralNil(token: Literal) : ExpressionLiteral(token) {
    init {
        assert(token.literalType == Token.Literal.Type.NIL)
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}

internal object ParseLiteralNil : Parser<LiteralNil> {
    override fun invoke(tokens: List<Token>): LiteralNil {
        return LiteralNil(tokens.first() as Literal)
    }
}