package io.hamal.script.ast.expr

import io.hamal.script.ast.Expression
import io.hamal.script.ast.Parser
import io.hamal.script.ast.parseExpression
import io.hamal.script.token.Token

data class GroupedExpression(val expression: Expression) : Expression {
    internal object Parse : ParsePrefixExpression<GroupedExpression> {
        override fun invoke(ctx: Parser.Context): GroupedExpression {
            ctx.expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            ctx.advance()

            if(ctx.currentTokenType() != Token.Type.RightParenthesis){
                val result = GroupedExpression(ctx.parseExpression(Precedence.Lowest))
                ctx.advance()
                return result
            }
            return GroupedExpression(Nil())
        }
    }
}