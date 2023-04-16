package io.hamal.script.ast.expr

import io.hamal.lib.meta.math.Decimal
import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type

interface LiteralExpression : Expression

internal interface ParseLiteralExpression<EXPRESSION : LiteralExpression> : ParseExpression<EXPRESSION>

data class NumberLiteral(val value: Decimal) : LiteralExpression {
    constructor(value: Int) : this(Decimal(value))

    internal object Parse : ParseLiteralExpression<NumberLiteral> {
        override fun invoke(ctx: Parser.Context): NumberLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Number)
            return NumberLiteral(Decimal(token.value))
        }
    }
}

data class StringLiteral(val value: kotlin.String) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<StringLiteral> {
        override fun invoke(ctx: Parser.Context): StringLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.String)
            return StringLiteral(token.value)
        }
    }
}

class TrueLiteral : LiteralExpression {

    internal object Parse : ParseLiteralExpression<TrueLiteral> {
        override fun invoke(ctx: Parser.Context): TrueLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.True)
            return TrueLiteral()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()
}

class FalseLiteral : LiteralExpression {
    internal object Parse : ParseLiteralExpression<FalseLiteral> {
        override fun invoke(ctx: Parser.Context): FalseLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.False)
            return FalseLiteral()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()
}


class NilLiteral : LiteralExpression {
    internal object Parse : ParseLiteralExpression<NilLiteral> {
        override fun invoke(ctx: Parser.Context): NilLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Nil)
            return NilLiteral()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

}

