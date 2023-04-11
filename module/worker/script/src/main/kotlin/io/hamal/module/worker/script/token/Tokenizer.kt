package io.hamal.module.worker.script.token

import io.hamal.lib.meta.exception.IllegalStateException
import io.hamal.lib.meta.exception.throwIf
import io.hamal.module.worker.script.token.Token.*
import io.hamal.module.worker.script.token.Token.Literal.Type.*
import io.hamal.module.worker.script.token.Tokenizer.DefaultImpl
import io.hamal.module.worker.script.token.TokenizerUtil.isAlpha
import io.hamal.module.worker.script.token.TokenizerUtil.isDigit
import io.hamal.module.worker.script.token.TokenizerUtil.isQuote
import io.hamal.module.worker.script.token.TokenizerUtil.isUnderscore

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
                isAtEnd() -> EOF(tokenLine(), tokenPosition())
                isHexNumber() -> nextHexNumber()
                isNumber() -> nextNumber()
                isString() -> nextString()
                else -> {
                    advance()
                    nextOperator() ?: run {
                        while (!isAtEnd() && peek() != '(' && (isAlpha(peek()) || isDigit(peek()) || isUnderscore(peek()))) {
                            advance()
                        }
                        nextBoolean()
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

private fun DefaultImpl.tokenPosition() = TokenPosition(linePosition - buffer.length + 1)

private fun DefaultImpl.tokenLine() = TokenLine(line)

private fun DefaultImpl.tokenValue() = TokenValue(buffer.toString())

internal fun DefaultImpl.isAtEnd() = index >= code.length

internal fun DefaultImpl.canPeekNext() = index < code.length - 1

internal fun DefaultImpl.peek(): Char {
    throwIf(isAtEnd()) { IllegalStateException("Can not read after end of code") }
    return code[index]
}

internal fun DefaultImpl.peekPrev(): Char {
    throwIf(index == 0) { IllegalStateException("Can not read before start of code") }
    throwIf(index >= code.length) { IllegalStateException("Can not read after end of code") }
    return code[index - 1]
}

internal fun DefaultImpl.peekNext(): Char {
    throwIf(index + 1 >= code.length) { IllegalStateException("Can not read after end of code") }
    return code[index + 1]
}

internal fun DefaultImpl.advance(): DefaultImpl {
    throwIf(index + 1 > code.length) { IllegalStateException("Can not read after end of code") }
    linePosition++
    buffer.append(code[index++])
    return this
}

internal fun DefaultImpl.advanceUntilWhitespace(): DefaultImpl {
    while (!isAtEnd() && !TokenizerUtil.isWhitespace(peek())) {
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
    return Literal(NUMBER, tokenLine(), tokenPosition(), tokenValue())
}

internal fun DefaultImpl.nextHexNumber(): Token {
    assert(peek() == '0' && peekNext() == 'x')
    advance(); // 0
    advance(); // x
    while (!isAtEnd() && TokenizerUtil.isHexChar(peek())) {
        advance();
    }
    return Literal(HEX_NUMBER, tokenLine(), tokenPosition(), tokenValue())
}

internal fun DefaultImpl.nextString(): Token {
    assert(isQuote(peek()))
    advance()
    var newLineCounter = 0
    while (true) {
        if (isAtEnd()) {
            return Error(tokenLine(), tokenPosition(), TokenValue("Unterminated string"))
        }

        if (isQuote(peek()) && peekPrev() != '\\') {
            break
        }

        if (peek() == '\n') {
            newLineCounter++
        }

        advance()
    }

    return Literal(STRING, tokenLine(), tokenPosition(), TokenValue(buffer.substring(1, buffer.length)))
}

internal fun DefaultImpl.nextIdentifierOrKeyword(): Token {
    return asKeyword() ?: Identifier(tokenLine(), tokenPosition(), tokenValue())
}

val keywordMapping = Keyword.Type.values().associateBy { it.value }

private fun DefaultImpl.asKeyword(): Keyword? {
    val value = buffer.toString()
    return keywordMapping[value]
        ?.let { Keyword(it, tokenLine(), tokenPosition(), TokenValue(value)) }
}

val operatorMapping = Operator.Type.values().associateBy { it.value }

private fun DefaultImpl.nextOperator(): Operator? {
    val value = buffer.toString()
    return operatorMapping[value]
        ?.let { Operator(it, tokenLine(), tokenPosition(), TokenValue(value)) }
}

private fun DefaultImpl.nextBoolean(): Literal? {
    return when (val value = buffer.toString()) {
        "true" -> Literal(BOOLEAN_TRUE, tokenLine(), tokenPosition(), TokenValue(value))
        "false" -> Literal(BOOLEAN_FALSE, tokenLine(), tokenPosition(), TokenValue(value))
        else -> null
    }
}
