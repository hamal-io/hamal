package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.FalseLiteral.ParseFalseLiteral
import io.hamal.module.worker.script.ast.expr.Identifier.ParseIdentifier
import io.hamal.module.worker.script.ast.expr.NilLiteral.ParseNilLiteral
import io.hamal.module.worker.script.ast.expr.StringLiteral.ParseStringLiteral
import io.hamal.module.worker.script.ast.expr.TrueLiteral.ParseTrueLiteral
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Token.Type

fun parse(tokens: List<Token>): List<Statement> {
    val parser = Parser.DefaultImpl(ArrayDeque(tokens))
    return parser.parse()
}

interface Parser {

    fun parse(): List<Statement>

    class DefaultImpl(
        internal val tokens: ArrayDeque<Token>
    ) : Parser {
        override fun parse(): List<Statement> {
            val result = mutableListOf<Statement>()
            while (tokens.isNotEmpty()) {
                result.add(parseStatement())
                if (peekCurrentToken().type == Type.Eof) {
                    break
                }
            }
            return result
        }
    }
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

private fun Parser.DefaultImpl.parseStatement(): Statement = StatementExpression(parseExpression())

private val prefixParseFnMapping = mapOf(
    Type.TrueLiteral to ParseTrueLiteral,
    Type.FalseLiteral to ParseFalseLiteral,
    Type.NilLiteral to ParseNilLiteral,
    Type.StringLiteral to ParseStringLiteral,
    Type.Identifier to ParseIdentifier,
)

private fun Parser.DefaultImpl.parseExpression(precedence: Precedence = Precedence.Lowest): Expression {
    val currentToken = peekCurrentToken()
    var leftExpression = (prefixParseFnMapping[currentToken.type] ?: TODO())(tokens)
    return leftExpression
}

private fun Parser.DefaultImpl.peekCurrentToken() = this.tokens.first()