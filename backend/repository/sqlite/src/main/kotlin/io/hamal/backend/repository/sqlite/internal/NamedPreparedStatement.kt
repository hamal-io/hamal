package io.hamal.backend.repository.sqlite.internal

//Inspired by: https://github.com/axiom-data-science/jdbc-named-parameters/blob/master/src/main/java/com/axiomalaska/jdbc/NamedParameterPreparedStatement.java

import io.hamal.backend.repository.sqlite.internal.NamedPreparedStatement.ParseResult
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.util.TokenizerUtils
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.base.DomainId
import io.hamal.lib.domain.vo.base.DomainName
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.Instant


interface NamedPreparedStatement<STATEMENT> : AutoCloseable {
    val sql: String
    val orderedParameters: List<String>
    operator fun set(parameter: String, value: Boolean): STATEMENT
    operator fun set(parameter: String, value: Int): STATEMENT
    operator fun set(parameter: String, value: Long): STATEMENT
    operator fun set(parameter: String, value: Instant): STATEMENT
    operator fun set(parameter: String, value: String): STATEMENT
    operator fun set(parameter: String, value: SnowflakeId): STATEMENT
    operator fun set(parameter: String, value: CmdId): STATEMENT
    operator fun set(parameter: String, value: DomainId): STATEMENT
    operator fun set(parameter: String, value: DomainName): STATEMENT
    operator fun set(parameter: String, value: ByteArray): STATEMENT
    fun execute(): NamedResultSet?
    fun executeUpdate(): Int
    fun executeQuery(): NamedResultSet
    class ParseResult(val sql: String, val orderedParameters: List<String>)
}


class DefaultNamedPreparedStatement(
    internal val delegate: PreparedStatement,
    internal val parseResult: ParseResult
) : NamedPreparedStatement<DefaultNamedPreparedStatement> {

    internal val parametersSet = mutableSetOf<String>()

    companion object {
        private val cachedSql = KeyedOnce.default<String, ParseResult>()
        fun Connection.prepare(query: String): NamedPreparedStatement<DefaultNamedPreparedStatement> {
            return cachedSql(query) { Parser().parse(query) }
                .let {
                    val stmt = prepareStatement(it.sql)
                    DefaultNamedPreparedStatement(stmt, it)
                }
        }
    }


    override val sql: String get() = parseResult.sql
    override val orderedParameters: List<String> get() = parseResult.orderedParameters

    override fun set(parameter: String, value: Boolean): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setBoolean(it, value) }
        return this
    }

    override fun set(parameter: String, value: Int): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setInt(it, value) }
        return this
    }

    override fun set(parameter: String, value: Long): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setLong(it, value) }
        return this
    }

    override fun set(parameter: String, value: Instant): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setTimestamp(it, Timestamp.from(value)) }
        return this
    }

    override fun set(parameter: String, value: SnowflakeId): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setLong(it, value.value) }
        return this
    }

    override fun set(parameter: String, value: CmdId): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setBigDecimal(it, value.value.toBigDecimal()) }
        return this
    }

    override fun set(parameter: String, value: DomainId): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setLong(it, value.value.value) }
        return this
    }

    override fun set(parameter: String, value: DomainName): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setString(it, value.value) }
        return this
    }

    override fun set(parameter: String, value: ByteArray): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setBytes(it, value) }
        return this
    }

    override fun set(parameter: String, value: String): DefaultNamedPreparedStatement {
        parametersSet.add(parameter)
        parametersSet.add(parameter)
        parseResult.parameterIndexesOf(parameter).forEach { delegate.setString(it, value) }
        return this
    }

    override fun execute(): NamedResultSet? {
        ensureAllParametersSet()
        delegate.execute()
        val rs = delegate.resultSet ?: return null
        return DefaultNamedResultSet(rs)
    }

    override fun executeUpdate(): Int {
        ensureAllParametersSet()
        return delegate.executeUpdate()
    }

    override fun executeQuery(): NamedResultSet {
        ensureAllParametersSet()
        return DefaultNamedResultSet(delegate.executeQuery())
    }

    override fun close() {
        delegate.close()
    }

    private fun ParseResult.parameterIndexesOf(parameter: String): Iterable<Int> {
        val result = orderedParameters.mapIndexedNotNull { index, orderedParameter ->
            if (parameter == orderedParameter) {
                index + 1
            } else {
                null
            }
        }
        require(result.isNotEmpty()) { "Statement does not contain parameter $parameter" }
        return result
    }
}

internal fun DefaultNamedPreparedStatement.ensureAllParametersSet() {
    val diff = this.parseResult.orderedParameters.toSet()
        .subtract(parametersSet)
    require(diff.isEmpty()) { "Expected all named parameters to be set, but $diff are missing" }
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
            if (TokenizerUtils.isWhitespace(current) || current == ';' || current == ',' || current == ')') {
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