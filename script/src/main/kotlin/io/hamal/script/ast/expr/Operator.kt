package io.hamal.script.ast.expr

import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type
import io.hamal.script.token.Token.Type.Category

interface ParseOperator {
    operator fun invoke(ctx: Parser.Context): Operator
}


enum class Operator(
    val value: String,
    val tokenType: Type
) {
    Plus("+", Type.Plus),
    LessThan("<", Type.LeftAngleBracket),
    Group("(", Type.LeftParenthesis),
    Minus("-", Type.Minus),
    ShiftLeft("<<", Type.LeftAngleBracketLeftAngleBracket),
    ShiftRight("<<", Type.RightAngleBracketRightAngleBracket),
    ;

    companion object {
        private val operatorMapping = Operator.values()
            .onEach { require(it.tokenType.category == Category.Operator) }
            .associateBy { it.tokenType }

        fun from(type: Type) = operatorMapping[type]!!
    }

    internal object Parse : ParseOperator {
        override fun invoke(ctx: Parser.Context): Operator {
            val result = operatorMapping[ctx.currentTokenType()]!!
            ctx.advance()
            return result
        }
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
    Type.LeftParenthesis to Precedence.Call,
    Type.Minus to Precedence.Plus,
    Type.Plus to Precedence.Plus
)

internal fun Parser.Context.currentPrecedence() =
    precedenceMapping[currentTokenType()] ?: Precedence.Lowest

internal fun Parser.Context.nextPrecedence() =
    precedenceMapping[nextTokenType()] ?: Precedence.Lowest