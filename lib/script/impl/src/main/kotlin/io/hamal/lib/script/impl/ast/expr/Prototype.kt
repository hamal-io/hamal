package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseStatement
import io.hamal.lib.script.impl.ast.stmt.BlockStatement
import io.hamal.lib.script.impl.token.Token.Type.*
import io.hamal.lib.script.impl.token.Token.Type.Function

class PrototypeLiteral(
    val identifier: IdentifierLiteral,
    val parameters: List<IdentifierLiteral>,
    val block: BlockStatement
) : LiteralExpression {

    internal object Parse : ParseLiteralExpression<PrototypeLiteral> {
        override fun invoke(ctx: Context): PrototypeLiteral {
            require(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Function)
            ctx.advance()

            val identifier = ctx.parseIdentifier()
            ctx.expectCurrentTokenTypToBe(LeftParenthesis)
            ctx.advance()

            val parameterIdentifiers = ctx.parseParameters()
            ctx.expectCurrentTokenTypToBe(RightParenthesis)
            ctx.advance()

            return PrototypeLiteral(
                identifier,
                parameterIdentifiers,
                ctx.parseBody()
            )
        }

        private fun Context.parseIdentifier(): IdentifierLiteral {
            return IdentifierLiteral.Parse(this)
        }

        private fun Context.parseParameters(): List<IdentifierLiteral> {
            val result = mutableListOf<IdentifierLiteral>()
            while (currentTokenType() != RightParenthesis) {
                expectCurrentTokenTypToBe(Identifier)
                result.add(parseIdentifier())
                if (currentTokenType() == Comma) {
                    advance()
                }
            }
            return result
        }

        private fun Context.parseBody(): BlockStatement {
            val statements = mutableListOf<Statement>()
            while (currentTokenType() != End) {
                require(currentTokenType() != Eof) {
                    "Expected end  but reached end of file"
                }
                parseStatement().let(statements::add)
            }
            expectCurrentTokenTypToBe(End)
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