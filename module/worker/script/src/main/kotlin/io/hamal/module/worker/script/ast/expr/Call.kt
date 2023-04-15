package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token.Type

class CallExpression(
    val function: Expression,
    val parameters: List<Expression>
) : Expression {
    internal object Parse: ParseInfixExpression{
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