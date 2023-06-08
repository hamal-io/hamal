package io.hamal.lib.script.impl.token

import io.hamal.lib.common.util.TokenizerUtils
import io.hamal.lib.common.util.TokenizerUtils.isAlpha
import io.hamal.lib.common.util.TokenizerUtils.isDigit
import io.hamal.lib.common.util.TokenizerUtils.isQuote
import io.hamal.lib.common.util.TokenizerUtils.isUnderscore
import io.hamal.lib.common.util.TokenizerUtils.isWhitespace
import io.hamal.lib.script.impl.token.Token.Type
import io.hamal.lib.script.impl.token.Token.Type.*
import io.hamal.lib.script.impl.token.Token.Type.Number

fun tokenize(code: String): List<Token> {
    val tokenizer = DefaultTokenizer(code)
    val result = mutableListOf<Token>()
    while (true) {
        val current = tokenizer.nextToken()
        result.add(current)
        if (current.type == Eof) {
            return result
        }
    }
}

interface Tokenizer {

    fun nextToken(): Token
}

class DefaultTokenizer(
    val code: String,
    internal var index: Int = 0,
    internal var line: Int = 1,
    internal var linePosition: Int = 0,
    internal val buffer: StringBuffer = StringBuffer(256)
) : Tokenizer {

    override fun nextToken(): Token {
        skipWhitespace()
        return when {
            isAtEnd() -> Token(Eof, tokenLine(), tokenPosition(), "EOF")
            isHexNumber() -> nextHexNumber()
            isNumber() -> nextNumber()
            isString() -> nextString()
            isCode() -> nextCode()
            else -> {
                advance()
                nextDelimiter()
                    ?: nextOperator()
                    ?: run {

                        while (!isAtEnd() && peek() != '(' && (isAlpha(peek()) || isDigit(peek()) || isUnderscore(
                                peek()
                            ))
                        ) {
                            advance()
                        }
                        nextLiteral()
                            ?: nextIdentifierOrKeyword()
                    }
            }
        }
    }
}

private fun DefaultTokenizer.isHexNumber() = peek() == '0' && peekNext() == 'x'

private fun DefaultTokenizer.isNumber() = isDigit(peek()) ||
        (canPeekNext() && peek() == '.' && isDigit(peekNext()))

private fun DefaultTokenizer.isString() = isQuote(peek())

private fun DefaultTokenizer.isCode() = peek() == '<' && (canPeekNext() && peekNext() == '[')

private fun DefaultTokenizer.tokenPosition() = linePosition - buffer.length + 1

private fun DefaultTokenizer.tokenLine() = line

private fun DefaultTokenizer.tokenValue() = buffer.toString()

internal fun DefaultTokenizer.isAtEnd(offset: Int = 0) = index + offset >= code.length

internal fun DefaultTokenizer.canPeekNext(offset: Int = 1) = index < code.length - offset

internal fun DefaultTokenizer.peek(): Char {
    check(!isAtEnd()) { "Can not read after end of code" }
    return code[index]
}

internal fun DefaultTokenizer.peekPrev(): Char {
    check(index > 0) { "Can not read before start of code" }
    check(index < code.length) { "Can not read after end of code" }
    return code[index - 1]
}

internal fun DefaultTokenizer.peekNext(offset: Int = 1): Char {
    check(index + offset < code.length) { "Can not read after end of code" }
    return code[index + offset]
}

internal fun DefaultTokenizer.advance(): DefaultTokenizer {
    check(index + 1 <= code.length) { "Can not read after end of code" }
    linePosition++
    buffer.append(code[index++])
    return this
}

internal fun DefaultTokenizer.advanceUntilWhitespace(): DefaultTokenizer {
    while (!isAtEnd() && !isWhitespace(peek())) {
        advance()
    }
    return this
}

internal fun DefaultTokenizer.skipWhitespace(): DefaultTokenizer {
    while (!isAtEnd()) {
        when (peek()) {
            '-' -> {
                if (canPeekNext() && peekNext() == '-') {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance()
                    }
                } else {
                    break
                }
            }

            '\n' -> {
                line++
                linePosition = 0
                advance()
            }

            ' ', '\r', '\t' -> {
                advance()
            }

            else -> break
        }
    }
    buffer.setLength(0)
    return this
}


internal fun DefaultTokenizer.nextNumber(): Token {
    assert(
        isDigit(peek()) ||
                (canPeekNext() && peek() == '.' && isDigit(peekNext()))
    )
    while (!isAtEnd() && isDigit(peek())) {
        advance()
    }
    if (!isAtEnd() && peek() == '.') {
        advance()
        while (!isAtEnd() && isDigit(peek())) {
            advance()
        }
    }
    return Token(Number, tokenLine(), tokenPosition(), tokenValue())
}

internal fun DefaultTokenizer.nextHexNumber(): Token {
    assert(peek() == '0' && peekNext() == 'x')
    advance() // 0
    advance() // x
    while (!isAtEnd() && TokenizerUtils.isHexChar(peek())) {
        advance()
    }
    return Token(HexNumber, tokenLine(), tokenPosition(), tokenValue())
}

internal fun DefaultTokenizer.nextString(): Token {
    assert(isQuote(peek()))
    advance() // remove first quote
    var newLineCounter = 0
    while (true) {
        if (isAtEnd()) {
            return Token(Error, tokenLine(), tokenPosition(), "Unterminated string")
        }

        if (isQuote(peek()) && peekPrev() != '\\') {
            break
        }

        if (peek() == '\n') {
            newLineCounter++
        }

        advance()
    }
    advance() // remove last quote
    return Token(Type.String, tokenLine(), tokenPosition(), buffer.substring(1, buffer.length - 1))
}

internal fun DefaultTokenizer.nextCode(): Token {
    advance()
    advance()
    var newLineCounter = 0
    while (true) {
        if (isAtEnd()) {
            return Token(Error, tokenLine(), tokenPosition(), "Unterminated code")
        }

        if (peek() == '>' && peekPrev() == ']') {
            break
        }

        if (peek() == '\n') {
            newLineCounter++
        }

        advance()
    }
    advance()
    return Token(Code, tokenLine(), tokenPosition(), buffer.substring(2, buffer.length - 2))
}


internal fun DefaultTokenizer.nextIdentifierOrKeyword(): Token {
    return asKeyword() ?: Token(Identifier, tokenLine(), tokenPosition(), tokenValue())
}

val keywordMapping = Type.values().filter { it.category == Category.Keyword }.associateBy { it.value }

private fun DefaultTokenizer.asKeyword(): Token? {
    val value = buffer.toString()
    return keywordMapping[value]
        ?.let { Token(it, tokenLine(), tokenPosition(), value) }
}

val operatorMapping = Type.values().filter { it.category == Category.Operator }.associateBy { it.value }

private fun DefaultTokenizer.nextOperator(): Token? {
    val lookAheadBuffer = StringBuffer(buffer)
    repeat(9) { idx ->
        if (!isAtEnd(idx)) {
            lookAheadBuffer.append(peekNext(idx))
        } else {
            lookAheadBuffer.append(' ')
        }
    }

    fun matchesBuffer(chars: CharArray): Boolean {
        return chars.filterIndexed { index, c -> lookAheadBuffer[index] != c }.isEmpty()
    }

    Token.Type.values()
        .filter { it.value.length > 1 }
        .filter { it.category == Category.Operator }
        .sortedByDescending { it.value.length }
        .forEach { type ->
            if (matchesBuffer(type.value.toCharArray())) {
                repeat(type.value.length - 1) {
                    advance()
                }
                return Token(type, tokenLine(), tokenPosition(), type.value)
            }
        }

    val value = buffer.toString()
    return operatorMapping[value]?.let { Token(it, tokenLine(), tokenPosition(), value) }
}

private fun DefaultTokenizer.nextLiteral(): Token? {
    return when (val value = buffer.toString()) {
        "true" -> Token(True, tokenLine(), tokenPosition(), value)
        "false" -> Token(False, tokenLine(), tokenPosition(), value)
        "nil" -> Token(Nil, tokenLine(), tokenPosition(), value)
        else -> null
    }
}

private fun DefaultTokenizer.nextDelimiter(): Token? {
    return when (buffer.toString()) {
        "," -> Token(Comma, tokenLine(), tokenPosition(), tokenValue())
        ";" -> Token(Semicolon, tokenLine(), tokenPosition(), tokenValue())
        else -> null
    }
}
