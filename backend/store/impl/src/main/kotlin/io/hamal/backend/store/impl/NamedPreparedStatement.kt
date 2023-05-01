package io.hamal.backend.store.impl

//Inspired by: https://github.com/axiom-data-science/jdbc-named-parameters/blob/master/src/main/java/com/axiomalaska/jdbc/NamedParameterPreparedStatement.java

import io.hamal.backend.store.impl.NamedPreparedStatement.ParseResult
import io.hamal.lib.util.TokenizerUtil


interface NamedPreparedStatement {
    fun setString(parameter: String, value: String)
    class ParseResult(val sql: String, val orderedParameters: List<String>)
}

internal class Parser {
    private var query: String = ""
    private var position: Int = 0
    private var resultBuffer: StringBuffer = StringBuffer()
    private var orderedParameters: MutableList<String> = mutableListOf()

    fun parse(query: String): ParseResult {
        initParser(query)

        while (!isAtEnd()) {
            val current = peek()
            when {
                current == ':' && peekNext() == ':' -> {
                    advance()
                    advance()
                }

                current == ':' && peekNext() != ':' -> {
                    skip()
                    orderedParameters.add(parseParameter())
                    resultBuffer.removeRange(resultBuffer.length, resultBuffer.length)
                    resultBuffer.append(" ? ")
                }

                current == '-' -> {
                    if (peekNext() == '-') {
                        while (!isAtEnd()) {
                            when (peek()) {
                                '\n' -> break
                                else -> skip()
                            }
                        }
                    }
                }

                current == '/' && peekNext() == '*' -> {
                    skip()
                    skip()
                    while (true) {
                        skipUntil('*')
                        if (current == '/') {
                            skip()
                            break
                        }
                    }

                }

                current == '\'' -> {
                    advance()
                    advanceUntil('\'')
                }

                current == '\"' -> {
                    advance()
                    advanceUntil('\"')
                }

                current == '\n' -> {
                    skip()
                    resultBuffer.append(" ")
                }

                else -> advance()
            }
        }

        return ParseResult(resultBuffer.replace(Regex("\\s{2,}"), " ").trim(), orderedParameters)
    }

    private fun parseParameter(): String {
        val resultBuilder = StringBuilder()
        while (!isAtEnd()) {
            val current = peek()
            if (TokenizerUtil.isWhitespace(current) || current == ';') {
                return resultBuilder.toString()
            } else {
                resultBuilder.append(skip())
            }
        }
        return resultBuilder.toString()
    }

    private fun initParser(query: String) {
        this.query = query
        this.position = 0
        this.resultBuffer = StringBuffer(query.length)
        this.orderedParameters = mutableListOf()
    }

    private fun advanceUntil(marker: Char) {
        while (!isAtEnd()) {
            if (peek() == marker) {
                advance()
                break
            }
            advance()
        }
    }

    private fun skipUntil(marker: Char) {
        while (!isAtEnd()) {
            if (peek() == marker) {
                skip()
                break
            }
            skip()
        }
    }

    private fun peek() = query[position]

    private fun peekNext() = query[position + 1]

    private fun advance(): Char {
        val result = query[position++]
        resultBuffer.append(result)
        return result
    }

    private fun skip(): Char {
        return query[position++]
    }

    private fun isAtEnd() = query.length - 1 < position

}

class DefaultNamedPreparedStatement : NamedPreparedStatement {
    private val orderedParameters = mutableListOf<String>()
    override fun setString(parameter: String, value: String) {
        TODO("Not yet implemented")
    }
}