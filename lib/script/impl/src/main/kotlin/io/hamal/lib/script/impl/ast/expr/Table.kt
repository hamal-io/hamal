package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token.Type.*

sealed interface FieldExpression {
    val valueExpression: Expression
}

data class IndexFieldExpression(
    val index: Int,
    override val valueExpression: Expression
) : FieldExpression {

    init {
        require(index > 0) { "First element starts with index 1" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IndexFieldExpression
        return index == other.index
    }

    override fun hashCode(): Int {
        return index
    }
}

data class KeyFieldExpression(
    val key: String,
    override val valueExpression: Expression
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

class TableConstructor(
    val fieldExpressions: List<FieldExpression>
) : Expression {

    internal object Parse : ParseExpression<TableConstructor> {
        override fun invoke(ctx: Parser.Context): TableConstructor {
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
                            index = index++,
                            valueExpression = expression
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
                            valueExpression = valueExpression
                        )
                    )
                }

                if (ctx.currentTokenType() == Comma) {
                    ctx.advance()
                }
            }
            ctx.expectCurrentTokenTypToBe(RightCurlyBracket)
            ctx.advance()
            return TableConstructor(fieldExpressions)
        }
    }

}