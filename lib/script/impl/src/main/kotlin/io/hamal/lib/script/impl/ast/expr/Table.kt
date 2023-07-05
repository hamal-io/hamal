package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.Token.Type.*
import io.hamal.lib.script.impl.token.Token.Type.Number

sealed interface FieldExpression {
    val value: Expression
}

class TableIndexLiteral(
    override val position: Position,
    val value: Int
) : LiteralExpression {
    init {
        require(value > 0) { "First element starts with index 1" }
    }

    internal object Parse : ParseLiteralExpression<TableIndexLiteral> {
        override fun invoke(ctx: Parser.Context): TableIndexLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Token.Type.Number)
            ctx.advance()
            return TableIndexLiteral(position, token.value.toInt())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TableIndexLiteral
        return value == other.value
    }

    override fun hashCode(): Int {
        return value
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

class TableKeyLiteral(
    override val position: Position,
    val value: String
) : LiteralExpression {
    init {
        require(value.trim().isNotEmpty()) { "ident can not be empty" }
    }

    internal object Parse : ParseLiteralExpression<TableKeyLiteral> {
        override fun invoke(ctx: Parser.Context): TableKeyLiteral {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            val token = ctx.currentToken()
            assert(token.type == Token.Type.Ident || token.type == Token.Type.String)
            ctx.advance()
            return TableKeyLiteral(position, token.value)
        }
    }

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TableKeyLiteral
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
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
    override val position: Position,
    val fieldExpressions: List<FieldExpression>
) : Expression {

    internal object Parse : ParseExpression<TableConstructorExpression> {
        override fun invoke(ctx: Parser.Context): TableConstructorExpression {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            ctx.expectCurrentTokenTypToBe(LeftCurlyBracket)
            ctx.advance()

            var index = 1
            val fieldExpressions = mutableListOf<FieldExpression>()
            while (ctx.currentTokenType() != RightCurlyBracket) {
                val expression = ctx.parseExpression(Precedence.Lowest)

                if (ctx.currentTokenType() != Equal) {
                    fieldExpressions.add(
                        IndexFieldExpression(
                            index = TableIndexLiteral(ctx.currentPosition(), index++),
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
            return TableConstructorExpression(position, fieldExpressions)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TableConstructorExpression
        return fieldExpressions == other.fieldExpressions
    }

    override fun hashCode(): Int {
        return fieldExpressions.hashCode()
    }
}

class TableAccessExpression(
    override val position: Position,
    val target: Expression,
    val key: Expression
) : Expression {

    object Parse : ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): Expression {
            return TableAccessExpression(
                position = ctx.currentPosition(),
                target = lhs,
                key = ctx.parseParameter()
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

    override fun toString(): String {
        return "${target}.${key}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TableAccessExpression
        if (target != other.target) return false
        return key == other.key
    }

    override fun hashCode(): Int {
        var result = target.hashCode()
        result = 31 * result + key.hashCode()
        return result
    }

}