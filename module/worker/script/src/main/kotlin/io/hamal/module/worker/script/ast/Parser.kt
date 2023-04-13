package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.Precedence
import io.hamal.module.worker.script.ast.expr.infixFn
import io.hamal.module.worker.script.ast.expr.nextPrecedence
import io.hamal.module.worker.script.ast.expr.prefixFn
import io.hamal.module.worker.script.ast.stmt.BlockStatement
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type.*

fun parse(tokens: List<Token>): BlockStatement {
    val parser = Parser.DefaultImpl
    return parser.parse(Parser.Context(ArrayDeque(tokens)))
}

interface Parser {

    fun parse(ctx: Context): BlockStatement

    object DefaultImpl : Parser {
        override fun parse(ctx: Context): BlockStatement {
            val statements = mutableListOf<Statement>()
            while (ctx.isNotEmpty()) {
                statements.add(ctx.parseStatement())
                ctx.advance()
                if (ctx.currentTokenType() == Eof) {
                    break
                }
            }
            return BlockStatement(statements)
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


private fun Parser.Context.parseStatement(): Statement = ExpressionStatement(parseExpression())

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