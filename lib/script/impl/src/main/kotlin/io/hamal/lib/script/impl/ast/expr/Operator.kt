package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.Operator.*
import io.hamal.lib.script.impl.ast.expr.Operator.Companion.operatorMapping
import io.hamal.lib.script.impl.token.Token.Type

interface ParseOperator {
    operator fun invoke(ctx: Context): Operator
}


enum class Operator(
    val tokenType: Type
) {
    And(Type.And),
    Call(Type.LeftParenthesis),
    Concat(Type.Dot_Dot),
    Divide(Type.Slash),
    Equals(Type.Equal_Equal),
    Exponential(Type.Carat),
    GreaterThan(Type.RightAngleBracket),
    GreaterThanEquals(Type.RightAngleBracket_Equal),
    IndexBasedAccess(Type.LeftBracket),
    KeyBasedAccess(Type.Dot),
    Length(Type.Hash),
    LessThan(Type.LeftAngleBracket),
    LessThanEquals(Type.LeftAngleBracket_Equal),
    Minus(Type.Minus),
    Modulo(Type.Percent),
    Multiply(Type.Asterisk),
    Not(Type.Not),
    NotEqual(Type.Tilde_Equal),
    Or(Type.Or),
    Plus(Type.Plus),
    ShiftLeft(Type.LeftAngleBracket_LeftAngleBracket),
    ShiftRight(Type.RightAngleBracket_RightAngleBracket),
    TableConstructor(Type.LeftCurlyBracket)
    ;

    companion object {
        val operatorMapping = Operator.values()
            .associateBy { it.tokenType }

        fun from(type: Type) = operatorMapping[type]!!
    }

    internal object Parse : ParseOperator {
        override fun invoke(ctx: Context): Operator {
            val result = operatorMapping[ctx.currentTokenType()]!!
            ctx.advance()
            return result
        }
    }

    override fun toString(): String = tokenType.toString()
}

internal enum class Precedence {
    Lowest,
    Or,              //  or
    And,             //  and
    Comparison,      //  <     >     <=    >=    ~=    ==
    Shift,           // << >>
    Concat,          //  ..
    Plus,            //  + -
    Factor,          //  * /  %
    Prefix,          // not # -
    Pow,             // ^
    Group,           // (
    Highest;
}

private val precedenceMapping = mapOf(
    Or to Precedence.Or,
    And to Precedence.And,
    Equals to Precedence.Comparison,
    GreaterThan to Precedence.Comparison,
    GreaterThanEquals to Precedence.Comparison,
    LessThan to Precedence.Comparison,
    LessThanEquals to Precedence.Comparison,
    NotEqual to Precedence.Comparison,
    ShiftLeft to Precedence.Shift,
    ShiftRight to Precedence.Shift,
    Concat to Precedence.Concat,
    Plus to Precedence.Plus,
    Minus to Precedence.Plus,
    Multiply to Precedence.Factor,
    Divide to Precedence.Factor,
    Modulo to Precedence.Factor,
    KeyBasedAccess to Precedence.Factor,
    IndexBasedAccess to Precedence.Factor,
    Call to Precedence.Group,
    Exponential to Precedence.Pow,
)

//internal fun precedenceOf(tokenType: Type) =
//    precedenceOf(operatorMapping[tokenType]!!)
//
//internal fun precedenceOf(op: Operator) =
//    precedenceMapping[op] ?: Precedence.Lowest

internal fun Context.currentPrecedence() =
    precedenceMapping[operatorMapping[currentTokenType()]] ?: Precedence.Lowest

//internal fun Context.nextPrecedence() =
//    precedenceMapping[operatorMapping[nextTokenType()]] ?: Precedence.Lowest