package io.hamal.script.ast.expr

import io.hamal.script.ast.Expression
import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type

class CallExpression(
    val prototype: Expression,
    val parameters: List<Expression>
) : Expression {
    internal object Parse: ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): Expression {
            return CallExpression(lhs, ctx.parseArguments())
        }

        private fun Parser.Context.parseArguments() : List<Expression>{
            expectCurrentTokenTypToBe(Type.LeftParenthesis)
            advance()

            if(currentTokenType() == Type.RightParenthesis){
                advance()
                return listOf()
            }
            TODO()
        }
    }
}