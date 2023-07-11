package io.hamal.lib.sqlite

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainName
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.sqlite.DefaultNamedPreparedStatement.Companion.prepare
import io.hamal.lib.sqlite.Transaction.AbortException
import logger
import java.math.BigInteger
import java.sql.DriverManager
import java.time.Instant
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KClass

class NamedPreparedStatementDelegate(
    val delegate: NamedPreparedStatement<*>
) {
    operator fun set(
        param: String,
        value: Boolean
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: Int
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: BigInteger
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: Limit
    ): NamedPreparedStatementDelegate {
        delegate[param] = value.value
        return this
    }

    operator fun set(
        param: String,
        value: Long
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: Instant
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: SnowflakeId
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: DomainId
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: DomainName
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: CmdId
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: ByteArray
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }

    operator fun set(
        param: String,
        value: String
    ): NamedPreparedStatementDelegate {
        delegate[param] = value
        return this
    }
}

class NamedPreparedStatementResultSetDelegate<RESULT : Any>(
    internal val delegate: NamedPreparedStatementDelegate
) {

    private var mapping: ((NamedResultSet) -> RESULT)? = null

    fun query(
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
    fun execute(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    )

    fun <T : Any> execute(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): T?

    fun executeUpdate(sql: String): Int
    fun executeUpdate(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ): Int

    fun <T : Any> executeQuery(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): List<T>

    fun executeQuery(
        sql: String,
        block: (NamedResultSet) -> Unit
    )

    fun <T : Any> executeQueryOne(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): T? = executeQuery(sql, block).firstOrNull()

    fun <T> tx(block: Transaction.() -> T): T
}

class DefaultConnection(
    owningClass: KClass<*>,
    url: String
) : Connection {

    val delegate: java.sql.Connection
    private val log = logger(owningClass)
    private val lock = ReentrantLock()

    init {
        delegate = DriverManager.getConnection(url)
        log.trace("Open sqlite connection with url: $url")
    }

    override val isOpen: Boolean get() = !delegate.isClosed
    override val isClosed: Boolean get() = delegate.isClosed

    override fun prepare(sql: String): NamedPreparedStatement<*> {
        log.trace("Prepare statement: $sql")
        val result = delegate.prepare(sql)
        log.trace("Prepared statement: ${result.sql}")
        return result
    }

    override fun execute(sql: String) {
        prepare(sql).use {
            log.debug("Execute: ${it.sql}")
            it.execute()
        }
    }

    override fun execute(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ) {
        prepare(sql).use {
            block(NamedPreparedStatementDelegate(it))
            log.debug("Execute: ${it.sql}")
            it.execute()
        }
    }

    override fun <T : Any> execute(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): T? {
        val it = prepare(sql)
        val delegate = NamedPreparedStatementResultSetDelegate<T>(
            NamedPreparedStatementDelegate(it)
        )
        block(delegate)
        log.debug("Execute : ${it.sql}")
        return it.execute()?.let(delegate::apply)?.firstOrNull()
    }

    override fun executeUpdate(sql: String): Int {
        return prepare(sql).use {
            log.debug("Execute update: ${it.sql}")
            it.executeUpdate()
        }
    }

    override fun executeUpdate(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ): Int {
        return prepare(sql).use {
            block(NamedPreparedStatementDelegate(it))
            log.debug("Execute update: ${it.sql}")
            it.executeUpdate()
        }
    }

    override fun <T : Any> executeQuery(
        sql: String,
        block: NamedPreparedStatementResultSetDelegate<T>.() -> NamedPreparedStatementResultSetDelegate<T>
    ): List<T> {
        return prepare(sql).use {
            val delegate =
                NamedPreparedStatementResultSetDelegate<T>(
                    NamedPreparedStatementDelegate(it)
                )
            block(delegate)
            log.debug("Execute query: ${it.sql}")
            delegate.apply(it.executeQuery())
        }
    }

    override fun executeQuery(sql: String, block: (NamedResultSet) -> Unit) {
        return prepare(sql).use {
            log.debug("Execute query: ${it.sql}")
            block(it.executeQuery())
        }
    }

    override fun <T> tx(block: Transaction.() -> T): T {
        return lock.withLock {
            delegate.autoCommit = false
            try {
                log.trace("Transaction started")
                val result = block(DefaultTransaction(this))
                delegate.commit()
                log.trace("Transaction committed")
                result
            } catch (a: AbortException) {
                log.info("Transaction aborted")
                delegate.rollback()
                throw a
            } catch (t: Throwable) {
                log.warn("Transaction rolled back due to $t")
                delegate.rollback()
                throw t
            } finally {
                log.trace("Transaction completed")
                delegate.autoCommit = true
            }
        }
    }

    override fun close() {
        delegate.close()
    }
}