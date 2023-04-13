package io.hamal.module.worker.script.ast.expr

import io.hamal.lib.meta.math.Decimal
import io.hamal.module.worker.script.ast.Literal
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token.Type

data class Number(val value: Decimal) : Literal {
    internal object Parse : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): Number {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.NumberLiteral)
            return Number(Decimal(token.value))
        }
    }
}

data class String(val value: kotlin.String) : Literal {
    internal object Parse : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): String {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.StringLiteral)
            return String(token.value)
        }
    }
}

class True : Literal {

    internal object Parse : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): True {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.TrueLiteral)
            return True()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()
}

class False : Literal {
    internal object Parse : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): False {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.FalseLiteral)
            return False()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()
}


class Nil : Literal {
    internal object ParseNilLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): Nil {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.NilLiteral)
            return Nil()
        }
    }

    override fun equals(other: Any?) = other != null && this::class == other::class

    override fun hashCode() = this::class.hashCode()

}

