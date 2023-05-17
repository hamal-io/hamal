package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.common.math.Decimal
import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token.Type

interface LiteralExpression : Expression

internal interface ParseLiteralExpression<EXPRESSION : LiteralExpression> : ParseExpression<EXPRESSION>

data class NumberLiteral(val value: Decimal) : LiteralExpression {
    constructor(value: Int) : this(Decimal(value))

    internal object Parse : ParseLiteralExpression<NumberLiteral> {
        override fun invoke(ctx: Context): NumberLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Number)
            ctx.advance()
            return NumberLiteral(Decimal(token.value))
        }
    }

    override fun toString() = value.toString()
}

data class StringLiteral(val value: String) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<StringLiteral> {
        override fun invoke(ctx: Context): StringLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.String)
            ctx.advance()
            return StringLiteral(token.value)
        }
    }

    override fun toString() = "'$value'"
}

object TrueLiteral : LiteralExpression {

    internal object Parse : ParseLiteralExpression<TrueLiteral> {
        override fun invoke(ctx: Context): TrueLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.True)
            ctx.advance()
            return TrueLiteral
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "true"
}

object FalseLiteral : LiteralExpression {
    internal object Parse : ParseLiteralExpression<FalseLiteral> {
        override fun invoke(ctx: Context): FalseLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.False)
            ctx.advance()
            return FalseLiteral
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "false"
}


object NilLiteral : LiteralExpression {
    internal object Parse : ParseLiteralExpression<NilLiteral> {
        override fun invoke(ctx: Context): NilLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Nil)
            ctx.advance()
            return NilLiteral
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "false"
}

