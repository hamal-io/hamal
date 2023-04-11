package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token

internal enum class Precedence {
    PRECEDENCE_OR,              //  or
    PRECEDENCE_AND,             //  and
    PRECEDENCE_COMPARISON,      //  <     >     <=    >=    ~=    ==
    PRECEDENCE_BITWISE_OR,      //  |
    PRECEDENCE_BITWISE_NOT,     //  ~
    PRECEDENCE_BITWISE_AND,     //  &
    PRECEDENCE_SHIFT,           // << >>
    PRECEDENCE_CONCAT,          //  ..
    PRECEDENCE_PLUS,            //  + -
    PRECEDENCE_FACTOR,          //  * / // %
    PRECEDENCE_UNARY,           // not # - ~
    PRECEDENCE_CARAT,           // ^
    PRECEDENCE_CALL             // ()
}

internal interface Parser<NODE : Node> {

    operator fun invoke(tokens: List<Token>): Node

//    class DefaultImpl : Parser {
//        override fun invoke(): List<Statement> {
//            TODO("Not yet implemented")
//        }
//
//    }
}