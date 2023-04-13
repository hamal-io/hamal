package io.hamal.module.worker.script.ast.expr

import io.hamal.lib.meta.math.Decimal
import io.hamal.module.worker.script.ParseException
import io.hamal.module.worker.script.ast.LiteralExpression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.parseStatement
import io.hamal.module.worker.script.ast.stmt.BlockStatement
import io.hamal.module.worker.script.token.Token.Type
import io.hamal.module.worker.script.token.Token.Type.*

data class NumberLiteral(val value: Decimal) : LiteralExpression {
    internal object ParseNumberLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): NumberLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.NumberLiteral)
            return NumberLiteral(Decimal(token.value))
        }
    }
}

data class StringLiteral(val value: String) : LiteralExpression {
    internal object ParseStringLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): StringLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.StringLiteral)
            return StringLiteral(token.value)
        }
    }
}

class TrueLiteral : LiteralExpression {
    internal object ParseTrueLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): TrueLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.TrueLiteral)
            return TrueLiteral()
        }
    }
}

class FalseLiteral : LiteralExpression {
    internal object ParseFalseLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): FalseLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.FalseLiteral)
            return FalseLiteral()
        }
    }
}


class NilLiteral : LiteralExpression {
    internal object ParseNilLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): NilLiteral {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.NilLiteral)
            return NilLiteral()
        }
    }
}


class FunctionLiteral(
    val identifier: Identifier,
    val parameters: List<Identifier>,
    val block: BlockStatement
) : LiteralExpression {
    internal object ParseFunctionLiteral : ParsePrefixExpression {
        override fun invoke(ctx: Parser.Context): FunctionLiteral {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Function)
            ctx.advance()

            val identifier = ctx.parseFunctionIdentifier()
            ctx.expectCurrentTokenTypToBe(LeftParenthesis)
            ctx.advance()

            val parameterIdentifiers = ctx.parseFunctionParameters()
            ctx.expectCurrentTokenTypToBe(RightParenthesis)
            ctx.advance()

            return FunctionLiteral(
                identifier,
                parameterIdentifiers,
                ctx.parseFunctionBody()
            )
        }

        private fun Parser.Context.parseFunctionIdentifier(): Identifier {
            val result = Identifier.ParseIdentifier(this)
            advance()
            return result
        }

        private fun Parser.Context.parseFunctionParameters(): List<Identifier> {
            val result = mutableListOf<Identifier>()
            while (currentTokenType() != RightParenthesis) {
                expectCurrentTokenTypToBe(Identifier)
                result.add(Identifier.ParseIdentifier(this))
                advance()
                if (currentTokenType() == Comma) {
                    advance()
                }
            }
            return result
        }

        private fun Parser.Context.parseFunctionBody(): BlockStatement {
            val statements = mutableListOf<Statement>()
            while (currentTokenType() != End) {
                if (currentTokenType() == Eof) {
                    throw ParseException("Expected end  but reached end of file")
                }
                parseStatement()?.let(statements::add)
            }
            expectCurrentTokenTypToBe(End)
            advance()
            return BlockStatement(statements)
        }
    }
}
