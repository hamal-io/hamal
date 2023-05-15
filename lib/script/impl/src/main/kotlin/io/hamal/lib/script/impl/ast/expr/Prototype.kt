package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseStatement
import io.hamal.lib.script.impl.ast.stmt.BlockStatement
import io.hamal.lib.script.impl.ast.stmt.Statement
import io.hamal.lib.script.impl.token.Token

class PrototypeLiteral(
    val identifier: IdentifierLiteral,
    val parameters: List<IdentifierLiteral>,
    val block: BlockStatement
) : LiteralExpression {

    internal object Parse : ParseLiteralExpression<PrototypeLiteral> {
        override fun invoke(ctx: Context): PrototypeLiteral {
            require(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Function)
            ctx.advance()

            val identifier = ctx.parseIdentifier()
            ctx.expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            ctx.advance()

            val parameterIdentifiers = ctx.parseParameters()
            ctx.expectCurrentTokenTypToBe(Token.Type.RightParenthesis)
            ctx.advance()

            return PrototypeLiteral(
                identifier,
                parameterIdentifiers,
                ctx.parseBody()
            )
        }

        private fun Context.parseIdentifier(): IdentifierLiteral {
            val result = IdentifierLiteral.Parse(this)
            advance()
            return result
        }

        private fun Context.parseParameters(): List<IdentifierLiteral> {
            val result = mutableListOf<IdentifierLiteral>()
            while (currentTokenType() != Token.Type.RightParenthesis) {
                expectCurrentTokenTypToBe(Token.Type.Identifier)
                result.add(IdentifierLiteral.Parse(this))
                advance()
                if (currentTokenType() == Token.Type.Comma) {
                    advance()
                }
            }
            return result
        }

        private fun Context.parseBody(): BlockStatement {
            val statements = mutableListOf<Statement>()
            while (currentTokenType() != Token.Type.End) {
                require(currentTokenType() != Token.Type.Eof) {
                    "Expected end  but reached end of file"
                }
                parseStatement().let(statements::add)
            }
            expectCurrentTokenTypToBe(Token.Type.End)
            advance()
            return BlockStatement(statements)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PrototypeLiteral

        if (identifier != other.identifier) return false
        return parameters == other.parameters
    }

    override fun hashCode(): Int {
        var result = identifier.hashCode()
        result = 31 * result + parameters.hashCode()
        return result
    }

    override fun toString(): String {
        return "$identifier($parameters)"
    }
}