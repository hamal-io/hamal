package io.hamal.backend.repository.impl.internal

interface Transaction {
    fun execute(sql: String)
    fun execute(sql: String, block: _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate)
    fun executeUpdate(sql: String): Int
    fun executeUpdate(sql: String, block: _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate): Int
    fun <T : Any> executeQuery(
        sql: String,
        block: _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>.() -> _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>
    ): List<T>

    fun abort()

    class AbortException : RuntimeException()
}

internal class DefaultTransaction(
    private val delegate: _root_ide_package_.io.hamal.backend.repository.impl.internal.Connection
) : Transaction {
    override fun execute(sql: String) = delegate.execute(sql)

    override fun execute(
        sql: String,
        block: _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate
    ) = delegate.execute(sql, block)

    override fun executeUpdate(sql: String): Int = delegate.executeUpdate(sql)

    override fun executeUpdate(
        sql: String,
        block: _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate
    ): Int = delegate.executeUpdate(sql, block)

    override fun <T : Any> executeQuery(
        sql: String,
        block: _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>.() -> _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>
    ): List<T> = delegate.executeQuery(sql, block)

    override fun abort() {
        throw Transaction.AbortException()
    }

}