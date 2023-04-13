package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.Precedence
import io.hamal.module.worker.script.ast.expr.infixFn
import io.hamal.module.worker.script.ast.expr.nextPrecedence
import io.hamal.module.worker.script.ast.expr.prefixFn
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type.*

fun parse(tokens: List<Token>): List<Statement> {
    val parser = Parser.DefaultImpl
    return parser.parse(Parser.Context(ArrayDeque(tokens)))
}

interface Parser {

    fun parse(ctx: Context): List<Statement>

    object DefaultImpl : Parser {
        override fun parse(ctx: Context): List<Statement> {
            val result = mutableListOf<Statement>()
            while (ctx.isNotEmpty()) {
                result.add(ctx.parseStatement())
                ctx.advance()
                if (ctx.currentTokenType() == Eof) {
                    break
                }
            }
            return result
        }
    }

    data class Context(val tokens: ArrayDeque<Token>) {
        fun advance() = tokens.removeFirst()

        fun currentToken() = this.tokens[0]
        fun nextToken() = this.tokens[1]

        fun currentTokenType() = currentToken().type
        fun nextTokenType() = nextToken().type

        fun isNotEmpty() = tokens.isNotEmpty()
    }
}


private fun Parser.Context.parseStatement(): Statement = StatementExpression(parseExpression())

internal fun Parser.Context.parseExpression(precedence: Precedence = Precedence.Lowest): Expression {
    var lhsExpression = prefixFn(currentTokenType())(this)
    while (!endOfExpression() && precedence < nextPrecedence()) {
        val infix = infixFn(nextTokenType()) ?: return lhsExpression
        advance()
        lhsExpression = infix(this, lhsExpression)
    }
    return lhsExpression
}

private fun Parser.Context.endOfExpression() = when (currentTokenType()) {
    Semicolon, LineBreak, Eof -> true
    else -> false
}