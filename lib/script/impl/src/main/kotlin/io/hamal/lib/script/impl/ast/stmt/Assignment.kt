package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.IdentifierLiteral
import io.hamal.lib.script.impl.ast.expr.Precedence
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token.Type.*

interface Assignment : Statement {
    val identifiers: List<IdentifierLiteral>
    val expressions: List<Expression>

    data class Global(
        override val identifiers: List<IdentifierLiteral>,
        override val expressions: List<Expression>
    ) : Assignment {
        constructor(identifier: IdentifierLiteral, expression: Expression) : this(
            listOf(identifier),
            listOf(expression)
        )

        init {
            assert(identifiers.isNotEmpty())
            assert(identifiers.size == expressions.size)
        }

        internal object Parse : ParseStatement<Global> {
            override fun invoke(ctx: Context): Global {
                ctx.expectCurrentTokenTypToBe(Identifier)
                val identifiers = ctx.parseIdentifiers()
                ctx.expectCurrentTokenTypToBe(Equal)
                ctx.advance()
                val expressions = ctx.parseExpressions()
                return Global(identifiers, expressions)
            }
        }
    }

    data class Local(
        override val identifiers: List<IdentifierLiteral>,
        override val expressions: List<Expression>
    ) : Assignment {
        constructor(identifier: IdentifierLiteral, expression: Expression) : this(
            listOf(identifier),
            listOf(expression)
        )

        init {
            assert(identifiers.isNotEmpty())
            assert(identifiers.size == expressions.size)
        }

        internal object Parse : ParseStatement<Local> {
            override fun invoke(ctx: Context): Local {
                ctx.expectCurrentTokenTypToBe(Local)
                ctx.advance()
                ctx.expectCurrentTokenTypToBe(Identifier)
                val identifiers = ctx.parseIdentifiers()
                ctx.expectCurrentTokenTypToBe(Equal)
                ctx.advance()
                val expressions = ctx.parseExpressions()
                return Local(identifiers, expressions)
            }
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

