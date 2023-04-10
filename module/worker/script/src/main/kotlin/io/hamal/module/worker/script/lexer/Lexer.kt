package io.hamal.module.worker.script.lexer

import io.hamal.lib.meta.exception.IllegalStateException
import io.hamal.lib.meta.exception.throwIf
import io.hamal.module.worker.script.lexer.Lexer.DefaultImpl
import io.hamal.module.worker.script.lexer.LexerUtil.isDigit
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.TokenLine
import io.hamal.module.worker.script.token.TokenLiteral
import io.hamal.module.worker.script.token.TokenPosition

interface Lexer {

    fun nextToken(): Token

    class DefaultImpl(
        val code: String,
        internal var index: Int = 0,

        internal var line: Int = 1,
        internal var linePosition: Int = 0,
        internal val buffer: StringBuffer = StringBuffer(256)
    ) : Lexer {
        override fun nextToken(): Token {
            skipWhitespace()

            if (isAtEnd()) {
                return Token.EOF(tokenLine(), tokenPosition())
            }

            return when {
                isDigit(peek()) -> nextNumber()
                else -> TODO("Not yet implemented")
            }
        }
    }
}

private fun DefaultImpl.tokenPosition() = TokenPosition(linePosition)

private fun DefaultImpl.tokenLine() = TokenLine(line)

private fun DefaultImpl.tokenLiteral() = TokenLiteral(buffer.toString())

internal fun DefaultImpl.isAtEnd() = index >= code.length

internal fun DefaultImpl.peek(): Char {
    throwIf(isAtEnd()) { IllegalStateException("Can not read after end of code") }
    return code[index]
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
    while (!isAtEnd() && !LexerUtil.isWhitespace(peek())) {
        advance()
    }
    return this
}

internal fun DefaultImpl.skipWhitespace(): DefaultImpl {
    while (!isAtEnd()) {
        when (peek()) {
            '-' -> {
                if (peekNext() == '-') {
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
    TODO()
}