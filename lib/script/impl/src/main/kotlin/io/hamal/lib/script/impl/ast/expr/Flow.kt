package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.expr.Precedence.Lowest
import io.hamal.lib.script.impl.ast.parseBlockStatement
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.ast.stmt.Block
import io.hamal.lib.script.impl.ast.stmt.DoStmt
import io.hamal.lib.script.impl.token.Token.Type.*

class ConditionalExpression(
    override val position: Position,
    val condition: Expression,
    val block: Block
) : Expression{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ConditionalExpression
        if (condition != other.condition) return false
        return block == other.block
    }

    override fun hashCode(): Int {
        var result = condition.hashCode()
        result = 31 * result + block.hashCode()
        return result
    }
}

class IfExpression(
    override val position: Position,
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
            val position = currentPosition()
            val condition = parseExpression(Lowest)
            expectCurrentTokenTypToBe(Then)
            advance()
            return ConditionalExpression(
                position = position,
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
                        position = currentPosition(),
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
                position = currentPosition(),
                condition = TrueLiteral(currentPosition()),
                block = parseBlockStatement()
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IfExpression
        return conditionalExpression == other.conditionalExpression
    }

    override fun hashCode(): Int {
        return conditionalExpression.hashCode()
    }
}

class ForLoopExpression(
    override val position: Position,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ForLoopExpression
        if (ident != other.ident) return false
        if (startExpression != other.startExpression) return false
        if (endExpression != other.endExpression) return false
        if (stepExpression != other.stepExpression) return false
        return block == other.block
    }

    override fun hashCode(): Int {
        var result = ident.hashCode()
        result = 31 * result + startExpression.hashCode()
        result = 31 * result + endExpression.hashCode()
        result = 31 * result + stepExpression.hashCode()
        result = 31 * result + block.hashCode()
        return result
    }
}
