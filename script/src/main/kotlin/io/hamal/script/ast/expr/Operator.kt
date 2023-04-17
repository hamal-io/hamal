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
    And("and", Type.And),
    Concat("..", Type.Dot_Dot),
    Divide("/", Type.Slash),
    Equals("==", Type.Equal_Equal),
    Exponential("^", Type.Carat),
    GreaterThan(">", Type.RightAngleBracket),
    GreaterThanEquals(">=", Type.RightAngleBracket_Equal),
    Group("(", Type.LeftParenthesis),
    Length("#", Type.Hash),
    LessThan("<", Type.LeftAngleBracket),
    LessThanEquals("<=", Type.LeftAngleBracket_Equal),
    Minus("-", Type.Minus),
    Multiply("*", Type.Asterisk),
    Not("not", Type.Not),
    NotEqual("~=", Type.Tilde_Equal),
    Or("or", Type.Or),
    Plus("+", Type.Plus),
    ShiftLeft("<<", Type.LeftAngleBracket_LeftAngleBracket),
    ShiftRight("<<", Type.RightAngleBracket_RightAngleBracket),
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
    Shift,           // << >>
    Concat,          //  ..
    Plus,            //  + -
    Factor,          //  * /
    Prefix,          // not # -
    Carat,           // ^
    Group,           // (
    Highest
}

private val precedenceMapping = mapOf(
    Type.LeftAngleBracket to Precedence.Comparison,
    Type.LeftParenthesis to Precedence.Group,
    Type.Minus to Precedence.Plus,
    Type.Plus to Precedence.Plus
)

internal fun Parser.Context.currentPrecedence() =
    precedenceMapping[currentTokenType()] ?: Precedence.Lowest

internal fun Parser.Context.nextPrecedence() =
    precedenceMapping[nextTokenType()] ?: Precedence.Lowest