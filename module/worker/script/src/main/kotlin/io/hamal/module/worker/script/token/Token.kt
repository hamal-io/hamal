package io.hamal.module.worker.script.token

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.module.worker.script.token.Token.Type.EOF
import io.hamal.module.worker.script.token.Token.Type.KEYWORD

class TokenLine(value: Int) : ValueObject.BaseImpl<Int>(value)
class TokenPosition(value: Int) : ValueObject.BaseImpl<Int>(value)
class TokenLiteral(value: String) : ValueObject.BaseImpl<String>(value)

sealed class Token(
    val type: Type,
    val line: TokenLine,
    val position: TokenPosition,
    val literal: TokenLiteral
) {

    class EOF(
        line: TokenLine,
        position: TokenPosition
    ) : Token(EOF, line, position, TokenLiteral("EOF"))

    class Keyword(
        val keywordType: Keyword.Type,
        line: TokenLine,
        position: TokenPosition,
        literal: TokenLiteral
    ) : Token(KEYWORD, line, position, literal) {

        enum class Type(value: String) {
            BREAK("break"),
            DO("do"),
            ELSE_IF("elseif"),
            ELSE("else"),
            END("end"),
            FOR("for"),
            FUNCTION("function"),
            IF("if"),
            IN("in"),
            LOCAL("local"),
            RETURN("return"),
            REPEAT("repeat"),
            THEN("then"),
            UNTIL("until"),
            WHILE("while"),
        }
    }

    enum class Type {
        DELIMITER,
        EOF,
        KEYWORD,
        IDENTIFIER,
        LITERAL,
        OPERATOR
    }

}