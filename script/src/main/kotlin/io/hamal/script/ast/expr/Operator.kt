package io.hamal.script.ast.expr

import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type

interface ParseOperator {
    operator fun invoke(ctx: Parser.Context): Operator
}

enum class Operator(val value: String) {
    Plus("+"),
    LessThan("<"),
    Minus("-");

    internal object Parse : ParseOperator {
        //@formatter:off
        override fun invoke(ctx: Parser.Context): Operator {
            return when (ctx.currentTokenType()) {
                Type.Plus -> { ctx.advance(); Plus }
                Type.Minus -> { ctx.advance(); Minus }
                Type.LeftAngleBracket ->{ ctx.advance(); LessThan }
                else -> TODO()
            }
        }
        //@formatter:on
    }

    override fun toString(): String = value
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
    Prefix,           // not # - ~
    Carat,           // ^
    Call,             // ()
    Highest
}

private val precedenceMapping = mapOf(
    Type.LeftAngleBracket to Precedence.Comparison,
    Type.Minus to Precedence.Plus,
    Type.Plus to Precedence.Plus,
    Type.LeftParenthesis to Precedence.Call
)

internal fun Parser.Context.currentPrecedence() =
    precedenceMapping[currentTokenType()] ?: Precedence.Lowest

internal fun Parser.Context.nextPrecedence() =
    precedenceMapping[nextTokenType()] ?: Precedence.Lowest