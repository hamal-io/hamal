package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.domain.math.Decimal
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token.Type

interface LiteralExpression : Expression

internal interface ParseLiteralExpression<EXPRESSION : LiteralExpression> : ParseExpression<EXPRESSION>

data class NumberLiteral(val value: Decimal) : LiteralExpression {
    constructor(value: Int) : this(Decimal(value))

    internal object Parse : ParseLiteralExpression<NumberLiteral> {
        override fun invoke(ctx: Context): NumberLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Number)
            return NumberLiteral(Decimal(token.value))
        }
    }

    override fun toString() = value.toString()
}

data class StringLiteral(val value: String) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<StringLiteral> {
        override fun invoke(ctx: Context): StringLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.String)
            return StringLiteral(token.value)
        }
    }

    override fun toString() = "'$value'"
}

class TrueLiteral : LiteralExpression {

    internal object Parse : ParseLiteralExpression<TrueLiteral> {
        override fun invoke(ctx: Context): TrueLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.True)
            return TrueLiteral()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "true"
}

class FalseLiteral : LiteralExpression {
    internal object Parse : ParseLiteralExpression<FalseLiteral> {
        override fun invoke(ctx: Context): FalseLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.False)
            return FalseLiteral()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "false"
}


class NilLiteral : LiteralExpression {
    internal object Parse : ParseLiteralExpression<NilLiteral> {
        override fun invoke(ctx: Context): NilLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Nil)
            return NilLiteral()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "false"
}

