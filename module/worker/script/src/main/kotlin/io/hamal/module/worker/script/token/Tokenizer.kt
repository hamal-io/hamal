package io.hamal.module.worker.script.token

import io.hamal.lib.meta.exception.IllegalStateException
import io.hamal.lib.meta.exception.throwIf
import io.hamal.module.worker.script.token.Token.Type
import io.hamal.module.worker.script.token.Token.Type.*
import io.hamal.module.worker.script.token.Tokenizer.DefaultImpl
import io.hamal.module.worker.script.token.TokenizerUtil.isAlpha
import io.hamal.module.worker.script.token.TokenizerUtil.isDigit
import io.hamal.module.worker.script.token.TokenizerUtil.isQuote
import io.hamal.module.worker.script.token.TokenizerUtil.isUnderscore

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
                isAtEnd() -> Token(Type.Eof, tokenLine(), tokenPosition(), TokenValue("EOF"))
                isHexNumber() -> nextHexNumber()
                isNumber() -> nextNumber()
                isString() -> nextString()
                else -> {
                    advance()
                    nextOperator() ?: run {
                        while (!isAtEnd() && peek() != '(' && (isAlpha(peek()) || isDigit(peek()) || isUnderscore(peek()))) {
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
    return Token(NumberLiteral, tokenLine(), tokenPosition(), tokenValue())
}

internal fun DefaultImpl.nextHexNumber(): Token {
    assert(peek() == '0' && peekNext() == 'x')
    advance(); // 0
    advance(); // x
    while (!isAtEnd() && TokenizerUtil.isHexChar(peek())) {
        advance();
    }
    return Token(HexNumberLiteral, tokenLine(), tokenPosition(), tokenValue())
}

internal fun DefaultImpl.nextString(): Token {
    assert(isQuote(peek()))
    advance() // remove first quote
    var newLineCounter = 0
    while (true) {
        if (isAtEnd()) {
            return Token(Error, tokenLine(), tokenPosition(), TokenValue("Unterminated string"))
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
    return Token(Type.StringLiteral, tokenLine(), tokenPosition(), TokenValue(buffer.substring(1, buffer.length - 1)))
}

internal fun DefaultImpl.nextIdentifierOrKeyword(): Token {
    return asKeyword() ?: Token(Identifier, tokenLine(), tokenPosition(), tokenValue())
}

val keywordMapping = Type.values().filter { it.category == Category.Keyword }.associateBy { it.value }

private fun DefaultImpl.asKeyword(): Token? {
    val value = buffer.toString()
    return keywordMapping[value]
        ?.let { Token(it, tokenLine(), tokenPosition(), TokenValue(value)) }
}

val operatorMapping = Type.values().filter { it.category == Category.Operator }.associateBy { it.value }

private fun DefaultImpl.nextOperator(): Token? {
    val value = buffer.toString()
    return operatorMapping[value]
        ?.let { Token(it, tokenLine(), tokenPosition(), TokenValue(value)) }
}

private fun DefaultImpl.nextLiteral(): Token? {
    return when (val value = buffer.toString()) {
        "true" -> Token(TrueLiteral, tokenLine(), tokenPosition(), TokenValue(value))
        "false" -> Token(FalseLiteral, tokenLine(), tokenPosition(), TokenValue(value))
        "nil" -> Token(NilLiteral, tokenLine(), tokenPosition(), TokenValue(value))
        else -> null
    }
}
