package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.Token.Type.*
import io.hamal.lib.script.impl.token.Token.Type.Number

sealed interface FieldExpression {
    val value: Expression
}


data class TableIndexLiteral(val value: Int) : LiteralExpression {
    init {
        require(value > 0) { "First element starts with index 1" }
    }

    internal object Parse : ParseLiteralExpression<TableIndexLiteral> {
        override fun invoke(ctx: Parser.Context): TableIndexLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Token.Type.Number)
            ctx.advance()
            return TableIndexLiteral(token.value.toInt())
        }
    }
}

data class IndexFieldExpression(
    val index: TableIndexLiteral,
    override val value: Expression
) : FieldExpression {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IndexFieldExpression
        return index == other.index
    }

    override fun hashCode(): Int {
        return index.hashCode()
    }
}

data class TableKeyLiteral(val value: String) : LiteralExpression {
    init {
        require(value.trim().isNotEmpty()) { "ident can not be empty" }
    }

    internal object Parse : ParseLiteralExpression<TableKeyLiteral> {
        override fun invoke(ctx: Parser.Context): TableKeyLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Token.Type.Ident || token.type == Token.Type.String)
            ctx.advance()
            return TableKeyLiteral(token.value)
        }
    }
}

data class KeyFieldExpression(
    val key: String,
    override val value: Expression
) : FieldExpression {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as KeyFieldExpression
        return key == other.key
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}

class TableConstructorExpression(
    val fieldExpressions: List<FieldExpression>
) : Expression {

    internal object Parse : ParseExpression<TableConstructorExpression> {
        override fun invoke(ctx: Parser.Context): TableConstructorExpression {
            require(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(LeftCurlyBracket)
            ctx.advance()

            var index = 1
            val fieldExpressions = mutableListOf<FieldExpression>()
            while (ctx.currentTokenType() != RightCurlyBracket) {
                val expression = ctx.parseExpression(Precedence.Lowest)

                if (ctx.currentTokenType() != Equal) {
                    fieldExpressions.add(
                        IndexFieldExpression(
                            index = TableIndexLiteral(index++),
                            value = expression
                        )
                    )
                } else {
                    ctx.expectCurrentTokenTypToBe(Equal)
                    ctx.advance()

                    val valueExpression = ctx.parseExpression(Precedence.Lowest)
                    require(expression is IdentifierLiteral)

                    fieldExpressions.add(
                        KeyFieldExpression(
                            key = expression.value,
                            value = valueExpression
                        )
                    )
                }

                if (ctx.currentTokenType() == Comma) {
                    ctx.advance()
                }
            }
            ctx.expectCurrentTokenTypToBe(RightCurlyBracket)
            ctx.advance()
            return TableConstructorExpression(fieldExpressions)
        }
    }

}

class TableAccessExpression(
    val ident: IdentifierLiteral,
    val parameter: Expression
) : Expression {

    object Parse : ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): Expression {
            require(lhs is IdentifierLiteral)
            return TableAccessExpression(
                ident = lhs,
                parameter = ctx.parseParameter()
            )
        }

        private fun Parser.Context.parseParameter(): Expression {
            return when {
                currentTokenType() == LeftBracket && nextTokenType() == Number -> parseIndex()
                else -> parseKey()
            }
        }

        private fun Parser.Context.parseIndex(): Expression {
            expectCurrentTokenTypToBe(LeftBracket)
            advance()
            expectCurrentTokenTypToBe(Number)
            val result = TableIndexLiteral.Parse(this)
            expectCurrentTokenTypToBe(RightBracket)
            advance()
            return result
        }

        private fun Parser.Context.parseKey(): Expression {
            if (currentTokenType() == Token.Type.Dot) {
                advance()
                expectCurrentTokenTypToBe(Ident)
                return TableKeyLiteral.Parse(this)
            }
            expectCurrentTokenTypToBe(LeftBracket)
            advance()
            expectCurrentTokenTypToBe(Token.Type.String)
            val result = TableKeyLiteral.Parse(this)
            expectCurrentTokenTypToBe(RightBracket)
            advance()
            return result
        }
    }
}