package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.parseBlockStatement
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.ast.stmt.BlockStatement
import io.hamal.lib.script.impl.token.Token.Type.*

data class ConditionalStatement(
    val condition: Expression,
    val body: BlockStatement
)

data class IfExpression(
    val conditionalStatement: List<ConditionalStatement>
) : Expression {

    internal object Parse : ParseExpression<IfExpression> {
        override fun invoke(ctx: Parser.Context): IfExpression {
            ctx.expectCurrentTokenTypToBe(If)
            ctx.advance()
            val condition = ctx.parseExpression(Precedence.Lowest)
            ctx.expectCurrentTokenTypToBe(Then)
            ctx.advance()
            val statement = ctx.parseBlockStatement()
            ctx.expectCurrentTokenTypToBe(End)
            ctx.advance()
            return IfExpression(listOf(ConditionalStatement(condition, statement)))
        }
    }

}