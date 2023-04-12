package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.FalseLiteral.ParseFalseLiteral
import io.hamal.module.worker.script.ast.expr.Identifier.ParseIdentifier
import io.hamal.module.worker.script.ast.expr.NilLiteral.ParseNilLiteral
import io.hamal.module.worker.script.ast.expr.NumberLiteral.ParseNumberLiteral
import io.hamal.module.worker.script.ast.expr.StringLiteral.ParseStringLiteral
import io.hamal.module.worker.script.ast.expr.TrueLiteral.ParseTrueLiteral
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type
import io.hamal.module.worker.script.token.Token.Type.Plus

fun parse(tokens: List<Token>): List<Statement> {
    val parser = Parser.DefaultImpl()
    return parser.parse(Parser.Context(ArrayDeque(tokens)))
}

interface Parser {

    fun parse(ctx: Context): List<Statement>

    class DefaultImpl : Parser {
        override fun parse(ctx: Context): List<Statement> {
            val result = mutableListOf<Statement>()
            while (ctx.isNotEmpty()) {
                result.add(ctx.parseStatement())
                if (ctx.currentTokenType() == Type.Eof) {
                    break
                }
            }
            return result
        }
    }

    data class Context(val tokens: ArrayDeque<Token>) {
        fun pop() = tokens.removeFirst()

        fun currentToken() = this.tokens[0]
        fun nextToken() = this.tokens[1]

        fun currentTokenType() = currentToken().type
        fun nextTokenType() = nextToken().type

        fun isNotEmpty() = tokens.isNotEmpty()
    }
}

internal interface ParsePrefixExpression<out EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context): EXPRESSION
}

internal interface ParseInfixExpression<out EXPRESSION : Expression> {
    operator fun invoke(ctx: Parser.Context, expression: Expression): EXPRESSION
}


internal enum class Precedence {
    Lowest,
    Or,              //  or
    And,             //  and
    Comparison,      //  <     >     <=    >=    ~=    ==
    BitwiseOr,      //  |
    BitwiseNot,     //  ~
    BitwiseAnd,     //  &
    Shift,           // << >>
    Concat,          //  ..
    Plus,            //  + -
    Factor,          //  * / // %
    Unary,           // not # - ~
    Carat,           // ^
    Call             // ()
}

private val precedenceMapping = mapOf(
    Plus to Precedence.Plus
)

private fun Parser.Context.parseStatement(): Statement = StatementExpression(parseExpression())

private val prefixParseFnMapping = mapOf(
    Type.TrueLiteral to ParseTrueLiteral,
    Type.FalseLiteral to ParseFalseLiteral,
    Type.NilLiteral to ParseNilLiteral,
    Type.StringLiteral to ParseStringLiteral,
    Type.Identifier to ParseIdentifier,
    Type.NumberLiteral to ParseNumberLiteral
)

private fun Parser.Context.parseExpression(precedence: Precedence = Precedence.Lowest): Expression {
    val currentToken = currentToken()
    var leftExpression = prefixParseFnMapping[currentTokenType()]!!(this)

    return leftExpression
}

private fun Parser.Context.currentPrecedence() = precedenceMapping[currentTokenType()]
private fun Parser.Context.nextPrecedence() = precedenceMapping[nextTokenType()]