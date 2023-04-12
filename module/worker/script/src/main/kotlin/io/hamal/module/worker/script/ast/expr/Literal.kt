package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.LiteralExpression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Visitor
import io.hamal.module.worker.script.token.Token.Type
import io.hamal.module.worker.script.value.*

class NumberLiteral(value: NumberValue) : LiteralExpression(value) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseNumberLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): NumberLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.NumberLiteral)
            return NumberLiteral(NumberValue(token.value))
        }
    }
}

class StringLiteral(value: StringValue) : LiteralExpression(value) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseStringLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): StringLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.StringLiteral)
            return StringLiteral(StringValue(token.value))
        }
    }
}

class TrueLiteral : LiteralExpression(TrueValue) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseTrueLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): TrueLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.TrueLiteral)
            return TrueLiteral()
        }
    }
}

class FalseLiteral : LiteralExpression(FalseValue) {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    internal object ParseFalseLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): FalseLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.FalseLiteral)
            return FalseLiteral()
        }
    }
}


class NilLiteral : LiteralExpression(NilValue) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    internal object ParseNilLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): NilLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.NilLiteral)
            return NilLiteral()
        }
    }
}

