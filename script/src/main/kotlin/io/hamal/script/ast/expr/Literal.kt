package io.hamal.script.ast.expr

import io.hamal.lib.meta.math.Decimal
import io.hamal.script.ast.Expression
import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type

interface LiteralExpression : Expression

internal interface ParseLiteralExpression<EXPRESSION : LiteralExpression> : ParsePrefixExpression<EXPRESSION>

data class Number(val value: Decimal) : LiteralExpression {
    constructor(value: Int) : this(Decimal(value))

    internal object Parse : ParseLiteralExpression<Number> {
        override fun invoke(ctx: Parser.Context): Number {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Number)
            return Number(Decimal(token.value))
        }
    }
}

data class String(val value: kotlin.String) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<String> {
        override fun invoke(ctx: Parser.Context): String {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.String)
            return String(token.value)
        }
    }
}

class True : LiteralExpression {

    internal object Parse : ParseLiteralExpression<True> {
        override fun invoke(ctx: Parser.Context): True {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.True)
            return True()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()
}

class False : LiteralExpression {
    internal object Parse : ParseLiteralExpression<False> {
        override fun invoke(ctx: Parser.Context): False {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.False)
            return False()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()
}


class Nil : LiteralExpression {
    internal object Parse : ParseLiteralExpression<Nil> {
        override fun invoke(ctx: Parser.Context): Nil {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Nil)
            return Nil()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

}

