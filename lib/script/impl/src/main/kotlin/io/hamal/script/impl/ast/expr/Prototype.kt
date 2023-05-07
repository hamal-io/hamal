package io.hamal.script.impl.ast.expr

import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.ast.parseStatement
import io.hamal.script.impl.ast.stmt.Block
import io.hamal.script.impl.ast.stmt.Statement
import io.hamal.script.impl.token.Token

class PrototypeLiteral(
    val identifier: Identifier,
    val parameters: List<Identifier>,
    val block: Block
) : LiteralExpression {

    internal object Parse : ParseLiteralExpression<PrototypeLiteral> {
        override fun invoke(ctx: Parser.Context): PrototypeLiteral {
            assert(ctx.isNotEmpty())
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

        private fun Parser.Context.parseIdentifier(): Identifier {
            val result = Identifier.Parse(this)
            advance()
            return result
        }

        private fun Parser.Context.parseParameters(): List<Identifier> {
            val result = mutableListOf<Identifier>()
            while (currentTokenType() != Token.Type.RightParenthesis) {
                expectCurrentTokenTypToBe(Token.Type.Identifier)
                result.add(Identifier.Parse(this))
                advance()
                if (currentTokenType() == Token.Type.Comma) {
                    advance()
                }
            }
            return result
        }

        private fun Parser.Context.parseBody(): Block {
            val statements = mutableListOf<Statement>()
            while (currentTokenType() != Token.Type.End) {
                if (currentTokenType() == Token.Type.Eof) {
                    throw io.hamal.script.impl.ScriptParseException("Expected end  but reached end of file")
                }
                parseStatement()?.let(statements::add)
            }
            expectCurrentTokenTypToBe(Token.Type.End)
            advance()
            return Block(statements)
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