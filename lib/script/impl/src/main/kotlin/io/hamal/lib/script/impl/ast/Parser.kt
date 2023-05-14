package io.hamal.lib.script.impl.ast

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ScriptParseException
import io.hamal.lib.script.impl.ast.expr.*
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

        fun currentOperator() = Operator.from(currentTokenType())

        fun expectCurrentTokenTypToBe(type: Type) {
            require(currentTokenType() == type) {
                ScriptParseException("Expected token to be $type but got ${currentTokenType()}")
            }
        }

        fun isNotEmpty() = tokens.isNotEmpty()
    }
}

internal fun Parser.Context.parseBlockStatement(): Block {
    val statements = mutableListOf<Statement>()
    while (currentTokenType() != Type.Eof && currentTokenType() != Type.End) {
        parseStatement()?.let(statements::add)
    }
    return Block(statements)
}

internal fun Parser.Context.parseStatement(): Statement? {
    val currentType = currentTokenType()
    return when {
        currentType == Type.Identifier && nextTokenType() == Type.Equal -> Assignment.Global.Parse(this)
        currentType == Type.Identifier && nextTokenType() == Type.LeftParenthesis -> Call.Parse(this)
        currentType == Type.Local -> Assignment.Local.Parse(this)
        currentType == Type.Function -> Prototype.Parse(this)
        currentType == Type.Return -> Return.Parse(this)
        else -> {
            val result = ExpressionStatement(parseExpression())
            advance()
            result
        }
    }
}

internal fun Parser.Context.parseExpression(precedence: Precedence = Precedence.Lowest): Expression {
    var lhsExpression: Expression = parseFn(currentTokenType())(this)
    while (!endOfExpression() && precedence < nextPrecedence()) {
        val infix = infixFn(nextTokenType()) ?: return lhsExpression
        advance()
        lhsExpression = infix(this, lhsExpression)
    }
    return lhsExpression
}

private fun Parser.Context.endOfExpression() = when (currentTokenType()) {
    Type.Semicolon, Type.Eof -> true
    else -> false
}