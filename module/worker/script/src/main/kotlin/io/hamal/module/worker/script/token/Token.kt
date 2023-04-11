package io.hamal.module.worker.script.token

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.module.worker.script.token.Token.Type.*

class TokenLine(value: Int) : ValueObject.BaseImpl<Int>(value)
class TokenPosition(value: Int) : ValueObject.BaseImpl<Int>(value)
class TokenValue(value: String) : ValueObject.BaseImpl<String>(value)

sealed class Token(
    val type: Type,
    val line: TokenLine,
    val position: TokenPosition,
    val value: TokenValue
) {

    class EOF(
        line: TokenLine,
        position: TokenPosition
    ) : Token(EOF, line, position, TokenValue("EOF"))

    class Error(
        line: TokenLine,
        position: TokenPosition,
        errorMessage: TokenValue
    ) : Token(ERROR, line, position, errorMessage)

    class Identifier(
        line: TokenLine,
        position: TokenPosition,
        value: TokenValue
    ) : Token(IDENTIFIER, line, position, value)

    class Literal(
        val literalType: Type,
        line: TokenLine,
        position: TokenPosition,
        value: TokenValue
    ) : Token(LITERAL, line, position, value) {
        enum class Type {
            BOOLEAN_FALSE,
            BOOLEAN_TRUE,
            HEX_NUMBER,
            NUMBER,
            NIL,
            STRING
        }
    }

    class Operator(
        val operatorType: Type,
        line: TokenLine,
        position: TokenPosition,
        value: TokenValue
    ) : Token(OPERATOR, line, position, value) {

        enum class Type(val value: String) {
            ASTERISK("*"),
            CARAT("^"),
            COLON(":"),
            DOT("."),
            EQUAL("="),
            HASH("#"),
            LEFT_ANGLE_BRACKET("<"),
            LEFT_BRACKET("["),
            MINUS("-"),
            PERCENT("%"),
            PLUS("+"),
            RIGHT_ANGLE_BRACKET(">"),
            RIGHT_BRACKET("]"),
            SLASH("/"),
            TILDE("~"),
        }
    }

    class Keyword(
        val keywordType: Type,
        line: TokenLine,
        position: TokenPosition,
        value: TokenValue
    ) : Token(KEYWORD, line, position, value) {

        enum class Type(val value: String) {
            AND("and"),
            BREAK("break"),
            DO("do"),
            ELSE("else"),
            ELSE_IF("elseif"),
            END("end"),
            FOR("for"),
            FUNCTION("function"),
            IF("if"),
            IN("in"),
            LOCAL("local"),
            NOR("nor"),
            NOT("not"),
            OR("or"),
            REPEAT("repeat"),
            RETURN("return"),
            THEN("then"),
            UNTIL("until"),
            WHILE("while"),
            XOR("xor"),
        }
    }

    enum class Type {
        DELIMITER,
        EOF,
        ERROR,
        KEYWORD,
        IDENTIFIER,
        LITERAL,
        OPERATOR
    }

}