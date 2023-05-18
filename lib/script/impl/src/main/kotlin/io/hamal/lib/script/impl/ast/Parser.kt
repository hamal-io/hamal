package io.hamal.lib.script.impl.ast

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.ast.stmt.*
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.Token.Type

fun parse(tokens: List<Token>): BlockStatement {
    return Parser.DefaultImpl.parse(
        Parser.Context(
            ArrayDeque(tokens)
        )
    )
}

interface Parser {

    fun parse(ctx: Context): BlockStatement

    object DefaultImpl : Parser {
        override fun parse(ctx: Context): BlockStatement = ctx.parseBlockStatement()
    }

    data class Context(val tokens: ArrayDeque<Token>) {
        fun advance() = tokens.removeFirst()

        fun currentToken() = this.tokens[0]
        fun nextToken() = this.tokens[1]

        fun currentTokenType() = currentToken().type
        fun nextTokenType() = nextToken().type

        fun currentOperator() = Operator.from(currentTokenType())

        fun expectCurrentTokenTypToBe(type: Type) {
            require(currentTokenType() == type) {
                "Expected token to be $type but got ${currentTokenType()}"
            }
        }

        fun isNotEmpty() = tokens.isNotEmpty()
    }
}

internal fun Parser.Context.parseBlockStatement(): BlockStatement {
    val statements = mutableListOf<Statement>()
    while (currentTokenType() != Type.Eof && currentTokenType() != Type.End) {
        parseStatement().let(statements::add)
        if (currentTokenType() == Type.Semicolon) {
            advance()
        }
    }
    return BlockStatement(statements)
}

internal fun Parser.Context.parseStatement(): Statement {
    return when {
        isGlobalAssignment() -> Assignment.Global.Parse(this)
        isCallExpression() -> Call.Parse(this)
        isLocalAssignment() -> Assignment.Local.Parse(this)
        isFunction() -> Prototype.Parse(this)
        isReturn() -> Return.Parse(this)
        else -> {
            ExpressionStatement(parseExpression())
        }
    }
}

private fun Parser.Context.isCallExpression() =
    currentTokenType() == Type.Identifier && nextTokenType() == Type.LeftParenthesis

private fun Parser.Context.isGlobalAssignment(): Boolean {
    return currentTokenType() == Type.Identifier && nextTokenType() == Type.Equal || nextTokenType() == Type.Comma
}

private fun Parser.Context.isLocalAssignment() = currentTokenType() == Type.Local

private fun Parser.Context.isFunction() = currentTokenType() == Type.Function

private fun Parser.Context.isReturn() = currentTokenType() == Type.Return

internal fun Parser.Context.parseExpression(precedence: Precedence = Precedence.Lowest): Expression {
    var lhsExpression: Expression = parseFn(currentTokenType())(this)
    while (!endOfExpression() && precedence < currentPrecedence()) {
        val infix = infixFn(currentTokenType()) ?: return lhsExpression
        lhsExpression = infix(this, lhsExpression)
    }
    return lhsExpression
}

private fun Parser.Context.endOfExpression() = when (currentTokenType()) {
    Type.Semicolon, Type.Eof -> true
    else -> false
}