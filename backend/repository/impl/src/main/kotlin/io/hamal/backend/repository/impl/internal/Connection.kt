package io.hamal.backend.repository.impl.internal

import io.hamal.backend.core.port.logger
import io.hamal.backend.repository.impl.internal.DefaultNamedPreparedStatement.Companion.prepare
import io.hamal.lib.KeyedOnce
import io.hamal.lib.RequestId
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import java.sql.DriverManager
import java.time.Instant

class NamedPreparedStatementDelegate(
    internal val delegate: io.hamal.backend.repository.impl.internal.NamedPreparedStatement<*>
) {
    operator fun set(param: String, value: Boolean): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: Int): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: Long): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: Instant): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: SnowflakeId): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: DomainId): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: RequestId): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: String): io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

}

class NamedPreparedStatementResultSetDelegate<RESULT : Any>(
    internal val delegate: io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate
) {

    private var mapping: ((io.hamal.backend.repository.impl.internal.NamedResultSet) -> RESULT)? = null

    fun with(
        block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate
    ): io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<RESULT> {
        block(delegate)
        return this
    }

    fun map(mapper: (io.hamal.backend.repository.impl.internal.NamedResultSet) -> RESULT): io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<RESULT> {
        this.mapping = mapper
        return this
    }

    internal fun apply(namedResultSet: io.hamal.backend.repository.impl.internal.NamedResultSet): List<RESULT> {
        val fn = mapping ?: return listOf()
        return namedResultSet.map(fn)
    }
}


interface Connection : AutoCloseable {
    val isOpen: Boolean
    val isClosed: Boolean
    fun prepare(sql: String): io.hamal.backend.repository.impl.internal.NamedPreparedStatement<*>
    fun execute(sql: String)
    fun execute(sql: String, block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate)
    fun executeUpdate(sql: String): Int
    fun executeUpdate(sql: String, block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate): Int
    fun <T : Any> executeQuery(
        sql: String,
        block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>
    ): List<T>

    fun <T : Any> executeQueryOne(
        sql: String,
        block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>
    ): T? = executeQuery(sql, block).firstOrNull()

    fun <T : Any> tx(block: io.hamal.backend.repository.impl.internal.Transaction.() -> T): T?
}

class DefaultConnection(name: String, url: String) : io.hamal.backend.repository.impl.internal.Connection {

    val delegate: java.sql.Connection
    private val log = logger(name)

    val statements = KeyedOnce.default<String, io.hamal.backend.repository.impl.internal.NamedPreparedStatement<*>>()

    init {
        delegate = DriverManager.getConnection(url)
        log.trace("Open sqlite connection with url: $url")
    }

    override val isOpen: Boolean get() = !delegate.isClosed
    override val isClosed: Boolean get() = delegate.isClosed

    override fun prepare(sql: String): io.hamal.backend.repository.impl.internal.NamedPreparedStatement<*> {
        return statements(sql) {
            val result = delegate.prepare(sql)
            log.trace("Prepared statement: ${result.sql}")
            result
        }.apply {
            this.clearParameter()
        }
    }

    override fun execute(sql: String) {
        prepare(sql).use {
            log.trace("Execute: ${it.sql}")
            it.execute()
        }
    }

    override fun execute(
        sql: String,
        block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate
    ) {
        prepare(sql).use {
            block(_root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate(it))
            log.trace("Execute: ${it.sql}")
            it.execute()
        }
    }

    override fun executeUpdate(sql: String): Int {
        return prepare(sql).use {
            log.trace("Execute update: ${it.sql}")
            it.executeUpdate()
        }
    }

    override fun executeUpdate(
        sql: String,
        block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate
    ): Int {
        return prepare(sql).use {
            block(_root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate(it))
            log.trace("Execute update: ${it.sql}")
            it.executeUpdate()
        }
    }

    override fun <T : Any> executeQuery(
        sql: String,
        block: io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>.() -> io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>
    ): List<T> {
        return prepare(sql).use {
            val delegate =
                _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementResultSetDelegate<T>(
                    _root_ide_package_.io.hamal.backend.repository.impl.internal.NamedPreparedStatementDelegate(it)
                )
            block(delegate)
            log.trace("Execute query: ${it.sql}")
            delegate.apply(_root_ide_package_.io.hamal.backend.repository.impl.internal.DefaultNamedResultSet(it.executeQuery()))
        }
    }

    override fun <T : Any> tx(block: io.hamal.backend.repository.impl.internal.Transaction.() -> T): T? {
        delegate.autoCommit = false
        return try {
            log.trace("Transaction started")
            val result = block(_root_ide_package_.io.hamal.backend.repository.impl.internal.DefaultTransaction(this))
            delegate.commit()
            log.trace("Transaction committed")
            result
        } catch (a: io.hamal.backend.repository.impl.internal.Transaction.AbortException) {
            log.info("Transaction aborted")
            delegate.rollback()
            null
        } catch (t: Throwable) {
            log.warn("Transaction rolled back due to $t")
            delegate.rollback()
            throw t
        } finally {
            log.trace("Transaction completed")
            delegate.autoCommit = true
        }
    }

    override fun close() {
        delegate.close()
    }
}