package io.hamal.backend.repository.impl.internal

interface Transaction {
    fun execute(sql: String)
    fun execute(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate)
    fun <T : Any> execute(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): T?

    fun executeUpdate(sql: String): Int
    fun executeUpdate(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate): Int
    fun <T : Any> executeQuery(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): List<T>

    fun <T : Any> executeQueryOne(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): T? = executeQuery(sql, block).firstOrNull()

    fun abort()

    class AbortException : RuntimeException()
}

internal class DefaultTransaction(
    private val delegate: Connection
) : Transaction {
    override fun execute(sql: String) = delegate.execute(sql)

    override fun execute(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ) = delegate.execute(sql, block)

    override fun <T : Any> execute(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): T? = delegate.execute(sql, block)

    override fun executeUpdate(sql: String): Int = delegate.executeUpdate(sql)

    override fun executeUpdate(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ): Int = delegate.executeUpdate(sql, block)

    override fun <T : Any> executeQuery(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): List<T> = delegate.executeQuery(sql, block)

    override fun abort() {
        throw Transaction.AbortException()
    }

}