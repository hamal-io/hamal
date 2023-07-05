package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token.Type

interface LiteralExpression : Expression

internal interface ParseLiteralExpression<EXPRESSION : LiteralExpression> : ParseExpression<EXPRESSION>

data class NumberLiteral(
    override val position: Position,
    val value: NumberValue
) : LiteralExpression {
    constructor(position: Position, value: Int) : this(position, NumberValue(value))

    internal object Parse : ParseLiteralExpression<NumberLiteral> {
        override fun invoke(ctx: Context): NumberLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Type.Number)
            ctx.advance()
            return NumberLiteral(position, NumberValue(token.value))
        }
    }

    override fun toString() = value.toString()
}

data class StringLiteral(
    override val position: Position,
    val value: String
) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<StringLiteral> {
        override fun invoke(ctx: Context): StringLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Type.String)
            ctx.advance()
            return StringLiteral(position, token.value)
        }
    }

    override fun toString() = "'$value'"
}

class TrueLiteral(
    override val position: Position
) : LiteralExpression {

    internal object Parse : ParseLiteralExpression<TrueLiteral> {
        override fun invoke(ctx: Context): TrueLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Type.True)
            ctx.advance()
            return TrueLiteral(position)
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "true"
}

class FalseLiteral(
    override val position: Position
) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<FalseLiteral> {
        override fun invoke(ctx: Context): FalseLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Type.False)
            ctx.advance()
            return FalseLiteral(position)
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "false"
}


class NilLiteral(
    override val position: Position
) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<NilLiteral> {
        override fun invoke(ctx: Context): NilLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Type.Nil)
            ctx.advance()
            return NilLiteral(position)
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

    override fun toString() = "nil"
}


data class CodeLiteral(
    override val position: Position,
    val value: String
) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<CodeLiteral> {
        override fun invoke(ctx: Context): CodeLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Type.Code)
            ctx.advance()
            return CodeLiteral(position, token.value)
        }
    }

    override fun toString() = "'$value'"
}
