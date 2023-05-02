package io.hamal.backend.store.impl

import io.hamal.backend.store.impl.DefaultNamedPreparedStatement.Companion.prepare
import java.sql.DriverManager

class NamedPreparedStatementDelegate constructor(
    internal val delegate: NamedPreparedStatement<*>
) {
    operator fun set(param: String, value: String): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }
}

interface Connection : AutoCloseable {
    val isOpen: Boolean
    val isClosed: Boolean
    fun prepare(sql: String): NamedPreparedStatement<*>
    fun execute(sql: String)
    fun execute(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate)
}


class DefaultConnection(url: String) : Connection {

    val delegate: java.sql.Connection

    init {
        delegate = DriverManager.getConnection(url)
    }

    override val isOpen: Boolean get() = !delegate.isClosed
    override val isClosed: Boolean get() = delegate.isClosed
    override fun prepare(sql: String) = delegate.prepare(sql)
    override fun execute(sql: String) {
        delegate.createStatement().use {
            it.execute(sql)
        }
    }

    override fun execute(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate) {
        prepare(sql).use {
            block(NamedPreparedStatementDelegate(it))
            it.execute()
        }
    }

    override fun close() {
        delegate.close()
    }
}