package io.hamal.script.token

import io.hamal.lib.meta.exception.IllegalStateException
import io.hamal.lib.meta.exception.throwIf
import io.hamal.script.token.Token.Type
import io.hamal.script.token.Token.Type.*
import io.hamal.script.token.Token.Type.Number
import io.hamal.script.token.Tokenizer.DefaultImpl
import io.hamal.script.token.TokenizerUtil.isAlpha
import io.hamal.script.token.TokenizerUtil.isDigit
import io.hamal.script.token.TokenizerUtil.isQuote
import io.hamal.script.token.TokenizerUtil.isUnderscore
import io.hamal.script.token.TokenizerUtil.isWhitespace

fun tokenize(code: String): List<Token> {
    val tokenizer = DefaultImpl(code)
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

    class DefaultImpl(
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
}

private fun DefaultImpl.isHexNumber() = peek() == '0' && peekNext() == 'x'

private fun DefaultImpl.isNumber() = isDigit(peek()) ||
        (canPeekNext() && peek() == '.' && isDigit(peekNext()))

private fun DefaultImpl.isString() = isQuote(peek())

private fun DefaultImpl.tokenPosition() = linePosition - buffer.length + 1

private fun DefaultImpl.tokenLine() = line

private fun DefaultImpl.tokenValue() = buffer.toString()

internal fun DefaultImpl.isAtEnd(offset: Int = 0) = index + offset >= code.length

internal fun DefaultImpl.canPeekNext(offset: Int = 1) = index < code.length - offset

internal fun DefaultImpl.peek(): Char {
    throwIf(isAtEnd()) { IllegalStateException("Can not read after end of code") }
    return code[index]
}

internal fun DefaultImpl.peekPrev(): Char {
    throwIf(index == 0) { IllegalStateException("Can not read before start of code") }
    throwIf(index >= code.length) { IllegalStateException("Can not read after end of code") }
    return code[index - 1]
}

internal fun DefaultImpl.peekNext(offset: Int = 1): Char {
    throwIf(index + offset >= code.length) { IllegalStateException("Can not read after end of code") }
    return code[index + offset]
}

internal fun DefaultImpl.advance(): DefaultImpl {
    throwIf(index + 1 > code.length) { IllegalStateException("Can not read after end of code") }
    linePosition++
    buffer.append(code[index++])
    return this
}

internal fun DefaultImpl.advanceUntilWhitespace(): DefaultImpl {
    while (!isAtEnd() && !isWhitespace(peek())) {
        advance()
    }
    return this
}

internal fun DefaultImpl.skipWhitespace(): DefaultImpl {
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


internal fun DefaultImpl.nextNumber(): Token {
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

internal fun DefaultImpl.nextHexNumber(): Token {
    assert(peek() == '0' && peekNext() == 'x')
    advance(); // 0
    advance(); // x
    while (!isAtEnd() && TokenizerUtil.isHexChar(peek())) {
        advance();
    }
    return Token(HexNumber, tokenLine(), tokenPosition(), tokenValue())
}

internal fun DefaultImpl.nextString(): Token {
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

internal fun DefaultImpl.nextIdentifierOrKeyword(): Token {
    return asKeyword() ?: Token(Identifier, tokenLine(), tokenPosition(), tokenValue())
}

val keywordMapping = Type.values().filter { it.category == Category.Keyword }.associateBy { it.value }

private fun DefaultImpl.asKeyword(): Token? {
    val value = buffer.toString()
    return keywordMapping[value]
        ?.let { Token(it, tokenLine(), tokenPosition(), value) }
}

val operatorMapping = Type.values().filter { it.category == Category.Operator }.associateBy { it.value }

private fun DefaultImpl.nextOperator(): Token? {
    return nextLookAheadOperator() ?: run {
        val value = buffer.toString()
        return operatorMapping[value]?.let { Token(it, tokenLine(), tokenPosition(), value) }
    }
}

private fun DefaultImpl.nextLookAheadOperator(): Token? {
    val buffer = StringBuffer(buffer)

    for (lookAheadIndex in 0 until 3) {
        if (!canPeekNext(lookAheadIndex)) {
            break
        }
        buffer.append(peekNext(lookAheadIndex))
        val candidate = operatorMapping[buffer.toString()]
        if (candidate != null) {
            val noMoreCharacters = (
                    canPeekNext(lookAheadIndex + 1) &&
                            (isWhitespace(peekNext(lookAheadIndex + 1)) ||
                            isAlpha(peekNext(lookAheadIndex + 1)) ||
                            isDigit(peekNext(lookAheadIndex + 1))
                            )
            )

            val atEnd = isAtEnd(lookAheadIndex + 1)
            if (noMoreCharacters || atEnd) {
                for (i in 0 until lookAheadIndex + 1) {
                    advance()
                }
                return Token(candidate, tokenLine(), tokenPosition(), buffer.toString())
            }
        }
    }

    return null
}

private fun DefaultImpl.nextLiteral(): Token? {
    return when (val value = buffer.toString()) {
        "true" -> Token(True, tokenLine(), tokenPosition(), value)
        "false" -> Token(False, tokenLine(), tokenPosition(), value)
        "nil" -> Token(Nil, tokenLine(), tokenPosition(), value)
        else -> null
    }
}

private fun DefaultImpl.nextDelimiter(): Token? {
    return when (buffer.toString()) {
        "," -> Token(Comma, tokenLine(), tokenPosition(), tokenValue())
        ";" -> Token(Semicolon, tokenLine(), tokenPosition(), tokenValue())
        else -> null
    }
}
