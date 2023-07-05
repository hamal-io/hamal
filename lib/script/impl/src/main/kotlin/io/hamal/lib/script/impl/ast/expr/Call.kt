package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token

class CallExpression(
    override val position: Position,
    val ident: Expression,
    val parameters: List<Expression>
) : Expression {
    internal object Parse : ParseInfixExpression {
        override fun invoke(ctx: Context, lhs: Expression): CallExpression {
            return CallExpression(
                ctx.currentPosition(),
                lhs,
                ctx.parseParameters()
            )
        }

        private fun Context.parseParameters(): List<Expression> {
            expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            advance()

            if (currentTokenType() == Token.Type.RightParenthesis) {
                advance()
                return listOf()
            }

            val result = mutableListOf<Expression>()
            while (currentTokenType() != Token.Type.RightParenthesis) {
                result.add(parseExpression(Precedence.Lowest))
                if (currentTokenType() == Token.Type.Comma) {
                    advance()
                }
            }
            advance()
            return result
        }
    }

    override fun toString() = "$ident(${parameters.joinToString(",") { it.toString() }})"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CallExpression

        if (ident != other.ident) return false
        return parameters == other.parameters
    }

    override fun hashCode(): Int {
        var result = ident.hashCode()
        result = 31 * result + parameters.hashCode()
        return result
    }

}