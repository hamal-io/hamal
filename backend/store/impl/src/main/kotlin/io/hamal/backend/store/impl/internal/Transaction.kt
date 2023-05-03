package io.hamal.backend.store.impl.internal

interface Transaction {
    fun execute(sql: String)
    fun execute(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate)
    fun executeUpdate(sql: String): Int
    fun executeUpdate(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate): Int
    fun <T : Any> executeQuery(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): List<T>

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