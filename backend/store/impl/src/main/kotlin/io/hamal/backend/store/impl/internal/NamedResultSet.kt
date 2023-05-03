package io.hamal.backend.store.impl.internal

import io.hamal.lib.RequestId
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import java.sql.ResultSet
import java.time.Instant

interface NamedResultSet : AutoCloseable {
    fun getBoolean(parameter: String): Boolean
    fun getInt(parameter: String): Int
    fun getLong(parameter: String): Long
    fun getString(parameter: String): String
    fun getInstant(parameter: String): Instant
    fun getSnowflakeId(parameter: String): SnowflakeId
    fun <DOMAIN_ID : DomainId> getDomainId(parameter: String, ctor: (SnowflakeId) -> DOMAIN_ID): DomainId
    fun getRequestId(parameter: String): RequestId

    fun <T : Any> map(mapper: (NamedResultSet) -> T): List<T>
}


class DefaultNamedResultSet(
    internal val delegate: ResultSet
) : NamedResultSet {
    override fun getBoolean(parameter: String): Boolean {
        ensureParameterExists(parameter)
        return delegate.getBoolean(parameter)
    }

    override fun getInt(parameter: String): Int {
        ensureParameterExists(parameter)
        return delegate.getInt(parameter)
    }

    override fun getLong(parameter: String): Long {
        ensureParameterExists(parameter)
        return delegate.getLong(parameter)
    }

    override fun getString(parameter: String): String {
        ensureParameterExists(parameter)
        return delegate.getString(parameter)
    }

    override fun getInstant(parameter: String): Instant {
        ensureParameterExists(parameter)
        return delegate.getTimestamp(parameter).toInstant()
    }

    override fun getSnowflakeId(parameter: String): SnowflakeId {
        ensureParameterExists(parameter)
        return SnowflakeId(delegate.getLong(parameter))
    }

    override fun <DOMAIN_ID : DomainId> getDomainId(parameter: String, ctor: (SnowflakeId) -> DOMAIN_ID): DomainId {
        ensureParameterExists(parameter)
        return ctor(getSnowflakeId(parameter))
    }

    override fun getRequestId(parameter: String): RequestId {
        ensureParameterExists(parameter)
        return RequestId(delegate.getBigDecimal(parameter).toBigInteger())
    }

    private fun ensureParameterExists(parameter: String) {
        val meta = delegate.metaData
        for (colIdx in 1..meta.columnCount) {
            if (meta.getColumnName(colIdx) == parameter) {
                return
            }
        }
        throw IllegalArgumentException("Parameter '$parameter' does not exist in result set")
    }

    override fun <T : Any> map(mapper: (NamedResultSet) -> T): List<T> {
        val result = mutableListOf<T>()
        while (delegate.next()) {
            result.add(mapper(this))
        }
        return result
    }

    override fun close() {
        delegate.close()
    }

}