package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.parseBlockStatement
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.ast.stmt.BlockStatement
import io.hamal.lib.script.impl.token.Token.Type.*

data class ConditionalExpression(
    val condition: Expression,
    val body: BlockStatement
)

data class IfExpression(
    val conditionalExpression: List<ConditionalExpression>
) : Expression {

    internal object Parse : ParseExpression<IfExpression> {
        override fun invoke(ctx: Parser.Context): IfExpression {
            val conditionals = mutableListOf<ConditionalExpression>()
            ctx.expectCurrentTokenTypToBe(If)
            ctx.advance()

            conditionals.add(ctx.parseIf())

            ctx.parseElse()?.let(conditionals::add)

            ctx.expectCurrentTokenTypToBe(End)
            ctx.advance()
            return IfExpression(conditionals)
        }


        private fun Parser.Context.parseIf(): ConditionalExpression {
            val condition = parseExpression(Precedence.Lowest)
            expectCurrentTokenTypToBe(Then)
            advance()
            return ConditionalExpression(
                condition = condition,
                body = parseBlockStatement()
            )
        }

        private fun Parser.Context.parseElse(): ConditionalExpression? {
            if (currentTokenType() != Else) {
                return null
            }
            advance()
            return ConditionalExpression(
                condition = TrueLiteral,
                body = parseBlockStatement()
            )
        }
    }
}
