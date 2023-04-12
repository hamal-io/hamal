package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.LiteralExpression
import io.hamal.module.worker.script.ast.ParsePrefixExpression
import io.hamal.module.worker.script.ast.Visitor
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type
import io.hamal.module.worker.script.value.FalseValue
import io.hamal.module.worker.script.value.NilValue
import io.hamal.module.worker.script.value.StringValue
import io.hamal.module.worker.script.value.TrueValue

class StringLiteral(value: StringValue) : LiteralExpression(value) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseStringLiteral : ParsePrefixExpression<StringLiteral> {
        override fun invoke(tokens: ArrayDeque<Token>): StringLiteral {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            assert(token.type == Type.StringLiteral)
            return StringLiteral(StringValue(token.value.value))
        }
    }
}

class TrueLiteral : LiteralExpression(TrueValue) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseTrueLiteral : ParsePrefixExpression<TrueLiteral> {
        override fun invoke(tokens: ArrayDeque<Token>): TrueLiteral {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            assert(token.type == Type.TrueLiteral)
            return TrueLiteral()
        }
    }
}

class FalseLiteral : LiteralExpression(FalseValue) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseFalseLiteral : ParsePrefixExpression<FalseLiteral> {
        override fun invoke(tokens: ArrayDeque<Token>): FalseLiteral {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            assert(token.type == Type.FalseLiteral)
            return FalseLiteral()
        }
    }
}


class NilLiteral : LiteralExpression(NilValue) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    internal object ParseNilLiteral : ParsePrefixExpression<NilLiteral> {
        override fun invoke(tokens: ArrayDeque<Token>): NilLiteral {
            assert(tokens.isNotEmpty())
            val token = tokens.removeFirst()
            assert(token.type == Type.NilLiteral)
            return NilLiteral()
        }
    }
}

