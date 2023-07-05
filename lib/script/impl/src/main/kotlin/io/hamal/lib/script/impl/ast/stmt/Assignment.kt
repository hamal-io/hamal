package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.IdentifierLiteral
import io.hamal.lib.script.impl.ast.expr.Precedence
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token.Type.*

interface Assignment : Statement {
    val identifiers: List<IdentifierLiteral>
    val expressions: List<Expression>

    class Global(
        override val position: Position,
        override val identifiers: List<IdentifierLiteral>,
        override val expressions: List<Expression>
    ) : Assignment {
        constructor(position: Position, ident: IdentifierLiteral, expression: Expression) : this(
            position,
            listOf(ident),
            listOf(expression)
        )

        init {
            assert(identifiers.isNotEmpty())
            assert(identifiers.size == expressions.size)
        }

        internal object Parse : ParseStatement<Global> {
            override fun invoke(ctx: Context): Global {
                val position = ctx.currentPosition()
                ctx.expectCurrentTokenTypToBe(Ident)
                val identifiers = ctx.parseIdentifiers()
                ctx.expectCurrentTokenTypToBe(Equal)
                ctx.advance()
                val expressions = ctx.parseExpressions()
                return Global(position, identifiers, expressions)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Global
            if (identifiers != other.identifiers) return false
            return expressions == other.expressions
        }

        override fun hashCode(): Int {
            var result = identifiers.hashCode()
            result = 31 * result + expressions.hashCode()
            return result
        }


    }

    class Local(
        override val position: Position,
        override val identifiers: List<IdentifierLiteral>,
        override val expressions: List<Expression>
    ) : Assignment {
        constructor(position: Position, ident: IdentifierLiteral, expression: Expression) : this(
            position,
            listOf(ident),
            listOf(expression)
        )

        init {
            assert(identifiers.isNotEmpty())
            assert(identifiers.size == expressions.size)
        }

        internal object Parse : ParseStatement<Local> {
            override fun invoke(ctx: Context): Local {
                val position = ctx.currentPosition()
                ctx.expectCurrentTokenTypToBe(Local)
                ctx.advance()
                ctx.expectCurrentTokenTypToBe(Ident)
                val identifiers = ctx.parseIdentifiers()
                ctx.expectCurrentTokenTypToBe(Equal)
                ctx.advance()
                val expressions = ctx.parseExpressions()
                return Local(position, identifiers, expressions)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Local
            if (identifiers != other.identifiers) return false
            return expressions == other.expressions
        }

        override fun hashCode(): Int {
            var result = identifiers.hashCode()
            result = 31 * result + expressions.hashCode()
            return result
        }
    }
}


private fun Context.parseIdentifiers(): List<IdentifierLiteral> {
    val result = mutableListOf<IdentifierLiteral>()
    do {
        if (currentTokenType() == Comma) {
            advance()
        }
        result.add(IdentifierLiteral.Parse(this))
    } while (currentTokenType() == Comma)
    return result
}

private fun Context.parseExpressions(): List<Expression> {
    val result = mutableListOf<Expression>()
    do {
        if (currentTokenType() == Comma) {
            advance()
        }
        result.add(parseExpression(Precedence.Lowest))
    } while (currentTokenType() == Comma)
    return result
}

