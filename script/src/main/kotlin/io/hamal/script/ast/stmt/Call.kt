package io.hamal.script.ast.stmt

import io.hamal.script.ast.Parser
import io.hamal.script.ast.expr.Expression
import io.hamal.script.ast.expr.Identifier
import io.hamal.script.token.Token

class Call(
    val identifier: Identifier,
    val parameters: List<Expression>
) : Statement {
    internal object Parse : ParseStatement<Call> {
        override fun invoke(ctx: Parser.Context): Call {
            val identifier = Identifier.Parse(ctx)
            ctx.advance()
            return Call(identifier, ctx.parseArguments())
        }
        private fun Parser.Context.parseArguments(): List<Expression> {
            expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            advance()

            if (currentTokenType() == Token.Type.RightParenthesis) {
                advance()
                return listOf()
            }
            TODO()
        }
    }
}