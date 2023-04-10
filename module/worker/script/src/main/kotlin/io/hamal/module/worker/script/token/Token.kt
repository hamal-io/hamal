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

    class Literal(
        val literalType: Type,
        line: TokenLine,
        position: TokenPosition,
        content: TokenValue
    ) : Token(LITERAL, line, position, content) {
        enum class Type {
            BOOLEAN_FALSE,
            BOOLEAN_TRUE,
            HEX_NUMBER,
            NUMBER,
            STRING
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
            FALSE("false"),
            FOR("for"),
            FUNCTION("function"),
            IF("if"),
            IN("in"),
            LOCAL("local"),
            NIL("nil"),
            NOT("not"),
            OR("or"),
            REPEAT("repeat"),
            RETURN("return"),
            THEN("then"),
            TRUE("true"),
            UNTIL("until"),
            WHILE("while"),
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