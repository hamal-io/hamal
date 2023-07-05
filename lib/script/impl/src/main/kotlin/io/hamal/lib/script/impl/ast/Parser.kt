package io.hamal.lib.script.impl.ast

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.expr.Precedence
import io.hamal.lib.script.impl.ast.expr.currentPrecedence
import io.hamal.lib.script.impl.ast.expr.infixFn
import io.hamal.lib.script.impl.ast.expr.parseFn
import io.hamal.lib.script.impl.ast.stmt.*
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.Token.Type

fun parse(tokens: List<Token>): Block {
    return Parser.DefaultImpl.parse(
        Parser.Context(
            ArrayDeque(tokens)
        )
    )
}

interface Parser {

    fun parse(ctx: Context): Block

    object DefaultImpl : Parser {
        override fun parse(ctx: Context): Block = ctx.parseBlockStatement()
    }

    data class Context(val tokens: ArrayDeque<Token>) {
        fun advance() = tokens.removeFirst()

        fun currentToken() = this.tokens[0]
        fun nextToken() = this.tokens[1]

        fun currentTokenType() = currentToken().type
        fun nextTokenType() = nextToken().type

        fun expectCurrentTokenTypToBe(type: Type) {
            require(currentTokenType() == type) {
                "Expected token to be $type but got ${currentTokenType()}"
            }
        }

        fun currentPosition(): Node.Position = Node.Position(currentToken().line, currentToken().position)

        fun isNotEmpty() = tokens.isNotEmpty()
    }
}

internal fun Parser.Context.parseBlockStatement(): Block {
    val statements = mutableListOf<Statement>()
    val position = currentPosition()

    while (
        currentTokenType() != Type.Eof &&
        currentTokenType() != Type.End &&
        currentTokenType() != Type.Else &&
        currentTokenType() != Type.ElseIf
    ) {
        parseStatement().let(statements::add)
        if (currentTokenType() == Type.Semicolon) {
            advance()
        }
    }
    return Block(position, statements)
}

internal fun Parser.Context.parseStatement(): Statement {
    val position = currentPosition()
    return when {
        isGlobalAssignment() -> Assignment.Global.Parse(this)
        isCallExpression() -> Call.Parse(this)
        isLocalAssignment() -> Assignment.Local.Parse(this)
        isFunction() -> Prototype.Parse(this)
        isReturn() -> Return.Parse(this)
        isBlock() -> DoStmt.Parse(this)
        else -> {
            ExpressionStatement(position, parseExpression())
        }
    }
}

private fun Parser.Context.isCallExpression() =
    currentTokenType() == Type.Ident && nextTokenType() == Type.LeftParenthesis

private fun Parser.Context.isGlobalAssignment(): Boolean {
    return currentTokenType() == Type.Ident && nextTokenType() == Type.Equal || nextTokenType() == Type.Comma
}

private fun Parser.Context.isLocalAssignment() = currentTokenType() == Type.Local

private fun Parser.Context.isFunction() = currentTokenType() == Type.Function

private fun Parser.Context.isReturn() = currentTokenType() == Type.Return
private fun Parser.Context.isBlock() = currentTokenType() == Type.Do

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