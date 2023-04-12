package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token

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
    Token.Type.Plus to Precedence.Plus
)

internal fun Parser.Context.currentPrecedence() = precedenceMapping[currentTokenType()] ?: Precedence.Lowest
internal fun Parser.Context.nextPrecedence() = precedenceMapping[nextTokenType()] ?: Precedence.Lowest