package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.Expression
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.Identifier
import io.hamal.lib.script.impl.ast.expr.Precedence
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token.Type

interface Assignment : Statement {
    val identifiers: List<Identifier>
    val expressions: List<Expression>

    data class Global(
        override val identifiers: List<Identifier>,
        override val expressions: List<Expression>
    ) : Assignment {
        constructor(identifier: Identifier, expression: Expression) : this(listOf(identifier), listOf(expression))

        init {
            assert(identifiers.isNotEmpty())
            assert(identifiers.size == expressions.size)
        }

        internal object Parse : ParseStatement<Global> {
            override fun invoke(ctx: Context): Global {
                ctx.expectCurrentTokenTypToBe(Type.Identifier)
                val identifiers = ctx.parseIdentifiers()
                ctx.expectCurrentTokenTypToBe(Type.Equal)
                ctx.advance()
                val expressions = ctx.parseExpressions()
                return Global(identifiers, expressions)
            }
        }
    }

    data class Local(
        override val identifiers: List<Identifier>,
        override val expressions: List<Expression>
    ) : Assignment {
        constructor(identifier: Identifier, expression: Expression) : this(listOf(identifier), listOf(expression))

        init {
            assert(identifiers.isNotEmpty())
            assert(identifiers.size == expressions.size)
        }

        internal object Parse : ParseStatement<Local> {
            override fun invoke(ctx: Context): Local {
                ctx.expectCurrentTokenTypToBe(Type.Local)
                ctx.advance()
                val identifiers = ctx.parseIdentifiers()
                ctx.expectCurrentTokenTypToBe(Type.Equal)
                ctx.advance()
                val expressions = ctx.parseExpressions()
                return Local(identifiers, expressions)
            }
        }
    }
}


private fun Context.parseIdentifiers(): List<Identifier> {
    val result = mutableListOf<Identifier>()
    do {
        if (currentTokenType() == Type.Comma) {
            advance()
        }
        result.add(Identifier.Parse(this))
        advance()
    } while (currentTokenType() == Type.Comma)
    return result
}

private fun Context.parseExpressions(): List<Expression> {
    val result = mutableListOf<Expression>()
    do {
        if (currentTokenType() == Type.Comma) {
            advance()
        }
        result.add(parseExpression(Precedence.Lowest))
        advance()
    } while (currentTokenType() == Type.Comma)
    return result
}

