package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.ExpressionLiteral
import io.hamal.module.worker.script.ast.NodeParser
import io.hamal.module.worker.script.ast.Visitor
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Literal.Type.*
import io.hamal.module.worker.script.value.FalseValue
import io.hamal.module.worker.script.value.NilValue
import io.hamal.module.worker.script.value.TrueValue

class TrueLiteral : ExpressionLiteral<TrueValue>(TrueValue) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object Parser : NodeParser<TrueLiteral> {
        override fun invoke(tokens: ArrayDeque<Token>): TrueLiteral {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            require(token is Token.Literal)
            require(token.literalType == BOOLEAN_TRUE)
            return TrueLiteral()
        }
    }
}

class FalseLiteral : ExpressionLiteral<FalseValue>(FalseValue) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object Parser : NodeParser<FalseLiteral> {
        override fun invoke(tokens: ArrayDeque<Token>): FalseLiteral {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            require(token is Token.Literal)
            require(token.literalType == BOOLEAN_FALSE)
            return FalseLiteral()
        }
    }
}


class NilLiteral : ExpressionLiteral<NilValue>(NilValue) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    internal object Parser : NodeParser<NilLiteral> {
        override fun invoke(tokens: ArrayDeque<Token>): NilLiteral {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            require(token is Token.Literal)
            require(token.literalType == NIL)
            return NilLiteral()
        }
    }
}

