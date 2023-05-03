package io.hamal.backend.store.impl.internal

import io.hamal.backend.core.port.logger
import io.hamal.backend.store.impl.internal.DefaultNamedPreparedStatement.Companion.prepare
import io.hamal.lib.KeyedOnce
import io.hamal.lib.RequestId
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import java.sql.DriverManager
import java.time.Instant

class NamedPreparedStatementDelegate(
    internal val delegate: NamedPreparedStatement<*>
) {
    operator fun set(param: String, value: Boolean): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: Int): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: Long): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: Instant): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: SnowflakeId): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: DomainId): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: RequestId): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(param: String, value: String): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

}

class NamedPreparedStatementResultSetDelegate<RESULT : Any>(
    internal val delegate: NamedPreparedStatementDelegate
) {

    private var mapping: ((NamedResultSet) -> RESULT)? = null

    fun with(
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ): NamedPreparedStatementResultSetDelegate<RESULT> {
        block(delegate)
        return this
    }

    fun map(mapper: (NamedResultSet) -> RESULT): NamedPreparedStatementResultSetDelegate<RESULT> {
        this.mapping = mapper
        return this
    }

    internal fun apply(namedResultSet: NamedResultSet): List<RESULT> {
        val fn = mapping ?: return listOf()
        return namedResultSet.map(fn)
    }
}


interface Connection : AutoCloseable {
    val isOpen: Boolean
    val isClosed: Boolean
    fun prepare(sql: String): NamedPreparedStatement<*>
    fun execute(sql: String)
    fun execute(sql: String, block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate)
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

    fun <T : Any> tx(block: Transaction.() -> T): T?
}

class DefaultConnection(name: String, url: String) : Connection {

    val delegate: java.sql.Connection
    private val log = logger(name)

    val statements = KeyedOnce.default<String, NamedPreparedStatement<*>>()

    init {
        delegate = DriverManager.getConnection(url)
        log.trace("Open sqlite connection with url: $url")
    }

    override val isOpen: Boolean get() = !delegate.isClosed
    override val isClosed: Boolean get() = delegate.isClosed

    override fun prepare(sql: String): NamedPreparedStatement<*> {
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
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ) {
        prepare(sql).use {
            block(NamedPreparedStatementDelegate(it))
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
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ): Int {
        return prepare(sql).use {
            block(NamedPreparedStatementDelegate(it))
            log.trace("Execute update: ${it.sql}")
            it.executeUpdate()
        }
    }

    override fun <T : Any> executeQuery(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): List<T> {
        return prepare(sql).use {
            val delegate = NamedPreparedStatementResultSetDelegate<T>(NamedPreparedStatementDelegate(it))
            block(delegate)
            log.trace("Execute query: ${it.sql}")
            delegate.apply(DefaultNamedResultSet(it.executeQuery()))
        }
    }

    override fun <T : Any> tx(block: Transaction.() -> T): T? {
        delegate.autoCommit = false
        return try {
            log.trace("Transaction started")
            val result = block(DefaultTransaction(this))
            delegate.commit()
            log.trace("Transaction committed")
            result
        } catch (a: Transaction.AbortException) {
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