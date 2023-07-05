package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.expr.Precedence.Lowest
import io.hamal.lib.script.impl.ast.parseBlockStatement
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.ast.stmt.Block
import io.hamal.lib.script.impl.ast.stmt.DoStmt
import io.hamal.lib.script.impl.token.Token.Type.*

data class ConditionalExpression(
    val condition: Expression,
    val block: Block
)

data class IfExpression(
    override val position: Node.Position,
    val conditionalExpression: List<ConditionalExpression>
) : Expression {
    internal object Parse : ParseExpression<IfExpression> {
        override fun invoke(ctx: Parser.Context): IfExpression {
            val position = ctx.currentPosition()
            val conditionals = mutableListOf<ConditionalExpression>()
            ctx.expectCurrentTokenTypToBe(If)
            ctx.advance()

            conditionals.add(ctx.parseIf())
            conditionals.addAll(ctx.parseElseIf())
            ctx.parseElse()?.let(conditionals::add)

            ctx.expectCurrentTokenTypToBe(End)
            ctx.advance()
            return IfExpression(position, conditionals)
        }


        private fun Parser.Context.parseIf(): ConditionalExpression {
            val condition = parseExpression(Lowest)
            expectCurrentTokenTypToBe(Then)
            advance()
            return ConditionalExpression(
                condition = condition,
                block = parseBlockStatement()
            )
        }

        private fun Parser.Context.parseElseIf(): List<ConditionalExpression> {
            val result = mutableListOf<ConditionalExpression>()
            while (currentTokenType() == ElseIf) {
                advance()
                val condition = parseExpression(Lowest)
                expectCurrentTokenTypToBe(Then)
                advance()
                result.add(
                    ConditionalExpression(
                        condition = condition,
                        block = parseBlockStatement()
                    )
                )
            }
            return result
        }

        private fun Parser.Context.parseElse(): ConditionalExpression? {
            if (currentTokenType() != Else) {
                return null
            }
            advance()
            return ConditionalExpression(
                condition = TrueLiteral(currentPosition()),
                block = parseBlockStatement()
            )
        }
    }
}

data class ForLoopExpression(
    override val position: Node.Position,
    val ident: IdentifierLiteral,
    val startExpression: Expression,
    val endExpression: Expression,
    val stepExpression: Expression,
    val block: DoStmt
) : Expression {
    internal object Parse : ParseExpression<ForLoopExpression> {
        override fun invoke(ctx: Parser.Context): ForLoopExpression {
            val position = ctx.currentPosition()
            ctx.expectCurrentTokenTypToBe(For)
            ctx.advance()

            val ident = ctx.parseIdentifier()
            ctx.expectCurrentTokenTypToBe(Equal)
            ctx.advance()
            val startExpression = ctx.parseExpression(Lowest)
            ctx.expectCurrentTokenTypToBe(Comma)
            ctx.advance()
            val endExpression = ctx.parseExpression(Lowest)


            val stepExpression = if (ctx.currentTokenType() == Comma) {
                ctx.advance()
                ctx.parseExpression(Lowest)
            } else {
                NumberLiteral(ctx.currentPosition(), 1)
            }

            return ForLoopExpression(
                position = position,
                ident = ident,
                startExpression = startExpression,
                endExpression = endExpression,
                stepExpression = stepExpression,
                block = DoStmt.Parse(ctx)
            )

        }
    }
}
