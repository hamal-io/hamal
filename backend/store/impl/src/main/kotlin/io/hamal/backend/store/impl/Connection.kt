package io.hamal.backend.store.impl

import io.hamal.backend.store.impl.DefaultNamedPreparedStatement.Companion.prepare
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

    private var mapping: ((NamedResultSet<RESULT>) -> RESULT)? = null

    fun with(
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ): NamedPreparedStatementResultSetDelegate<RESULT> {
        block(delegate)
        return this
    }

    fun map(mapper: (NamedResultSet<RESULT>) -> RESULT): NamedPreparedStatementResultSetDelegate<RESULT> {
        this.mapping = mapper
        return this
    }

    internal fun apply(namedResultSet: NamedResultSet<RESULT>): List<RESULT> {
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
}

class DefaultConnection(url: String) : Connection {

    val delegate: java.sql.Connection

    val statements = KeyedOnce.default<String, NamedPreparedStatement<*>>()

    init {
        delegate = DriverManager.getConnection(url)
    }

    override val isOpen: Boolean get() = !delegate.isClosed
    override val isClosed: Boolean get() = delegate.isClosed

    override fun prepare(sql: String): NamedPreparedStatement<*> {
        return statements(sql) {
            delegate.prepare(sql)
        }.apply {
            this.clearParameter()
        }
    }

    override fun execute(sql: String) {
        prepare(sql).use {
            it.execute()
        }
    }

    override fun execute(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ) {
        prepare(sql).use {
            block(NamedPreparedStatementDelegate(it))
            it.execute()
        }
    }

    override fun executeUpdate(sql: String): Int {
        return prepare(sql).use {
            it.executeUpdate()
        }
    }

    override fun executeUpdate(
        sql: String,
        block: NamedPreparedStatementDelegate.() -> NamedPreparedStatementDelegate
    ): Int {
        return prepare(sql).use {
            block(NamedPreparedStatementDelegate(it))
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
            delegate.apply(DefaultNamedResultSet(it.executeQuery()))
        }
    }

    override fun close() {
        delegate.close()
    }
}