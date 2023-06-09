package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseStatement
import io.hamal.lib.script.impl.ast.stmt.Block
import io.hamal.lib.script.impl.token.Token.Type.*
import io.hamal.lib.script.impl.token.Token.Type.Function

class PrototypeLiteral(
    val ident: IdentifierLiteral,
    val parameters: List<IdentifierLiteral>,
    val block: Block
) : LiteralExpression {

    internal object Parse : ParseLiteralExpression<PrototypeLiteral> {
        override fun invoke(ctx: Context): PrototypeLiteral {
            require(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Function)
            ctx.advance()

            val ident = if (ctx.currentTokenType() != LeftParenthesis) {
                val ident = ctx.parseIdentifier()
                ctx.expectCurrentTokenTypToBe(LeftParenthesis)
                ctx.advance()
                ident
            } else {
                ctx.advance()
                IdentifierLiteral("lambda")
            }

            val parameterIdentifiers = ctx.parseParameters()
            ctx.expectCurrentTokenTypToBe(RightParenthesis)
            ctx.advance()

            return PrototypeLiteral(
                ident,
                parameterIdentifiers,
                ctx.parseBody()
            )
        }


        private fun Context.parseParameters(): List<IdentifierLiteral> {
            val result = mutableListOf<IdentifierLiteral>()
            while (currentTokenType() != RightParenthesis) {
                expectCurrentTokenTypToBe(Ident)
                result.add(parseIdentifier())
                if (currentTokenType() == Comma) {
                    advance()
                }
            }
            return result
        }

        private fun Context.parseBody(): Block {
            val statements = mutableListOf<Statement>()
            while (currentTokenType() != End) {
                require(currentTokenType() != Eof) {
                    "Expected end  but reached end of file"
                }
                parseStatement().let(statements::add)
            }
            expectCurrentTokenTypToBe(End)
            advance()
            return Block(statements)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PrototypeLiteral

        if (ident != other.ident) return false
        return parameters == other.parameters
    }

    override fun hashCode(): Int {
        var result = ident.hashCode()
        result = 31 * result + parameters.hashCode()
        return result
    }

    override fun toString(): String {
        return "$ident($parameters)"
    }
}