package io.hamal.script.ast.expr

import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type
import kotlin.String

interface ParseOperator {
    operator fun invoke(ctx: Parser.Context) : Operator
}

enum class Operator(val value: String) {
    Plus("+"),
    Minus("-");
    internal object Parse: ParseOperator {
        override fun invoke(ctx: Parser.Context): Operator {
            return when(val type = ctx.currentTokenType()){
                Type.Plus -> { ctx.advance(); Plus }
                Type.Minus -> { ctx.advance(); Minus }
                else -> TODO()
            }
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
    Prefix,           // not # - ~
    Carat,           // ^
    Call             // ()
}

private val precedenceMapping = mapOf(
    Type.Plus to Precedence.Plus,
    Type.Minus to Precedence.Plus,
    Type.LeftParenthesis to Precedence.Call,
)

internal fun Parser.Context.currentPrecedence() = precedenceMapping[currentTokenType()] ?: io.hamal.script.ast.expr.Precedence.Lowest
internal fun Parser.Context.nextPrecedence() = precedenceMapping[nextTokenType()] ?: io.hamal.script.ast.expr.Precedence.Lowest