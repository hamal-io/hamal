package io.hamal.script.ast.expr

import io.hamal.script.ParseException
import io.hamal.script.ast.Parser
import io.hamal.script.ast.Statement
import io.hamal.script.ast.parseStatement
import io.hamal.script.ast.stmt.Block
import io.hamal.script.token.Token
import kotlin.String

class Function(
    val identifier: Identifier,
    val parameters: List<Identifier>,
    val block: Block
) : LiteralExpression {

    internal object Parse : ParseLiteralExpression<Function> {
        override fun invoke(ctx: Parser.Context): Function {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Function)
            ctx.advance()

            val identifier = ctx.parseFunctionIdentifier()
            ctx.expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            ctx.advance()

            val parameterIdentifiers = ctx.parseFunctionParameters()
            ctx.expectCurrentTokenTypToBe(Token.Type.RightParenthesis)
            ctx.advance()

            return Function(
                identifier,
                parameterIdentifiers,
                ctx.parseFunctionBody()
            )
        }

        private fun Parser.Context.parseFunctionIdentifier(): Identifier {
            val result = Identifier.Parse(this)
            advance()
            return result
        }

        private fun Parser.Context.parseFunctionParameters(): List<Identifier> {
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

        private fun Parser.Context.parseFunctionBody(): Block {
            val statements = mutableListOf<Statement>()
            while (currentTokenType() != Token.Type.End) {
                if (currentTokenType() == Token.Type.Eof) {
                    throw ParseException("Expected end  but reached end of file")
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

        other as Function

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