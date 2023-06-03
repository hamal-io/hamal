package io.hamal.backend.repository.sqlite.internal

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.vo.base.DomainId
import java.sql.ResultSet
import java.time.Instant

interface NamedResultSet : AutoCloseable {
    fun getBoolean(parameter: String): Boolean
    fun getInt(parameter: String): Int
    fun getLong(parameter: String): Long
    fun getString(parameter: String): String
    fun getInstant(parameter: String): Instant
    fun getSnowflakeId(parameter: String): SnowflakeId
    fun <DOMAIN_ID : DomainId> getDomainId(parameter: String, ctor: (SnowflakeId) -> DOMAIN_ID): DOMAIN_ID
    fun getCommandId(parameter: String): CommandId
    fun getBytes(parameter: String): ByteArray
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

    override fun <DOMAIN_ID : DomainId> getDomainId(parameter: String, ctor: (SnowflakeId) -> DOMAIN_ID): DOMAIN_ID {
        ensureParameterExists(parameter)
        return ctor(getSnowflakeId(parameter))
    }

    override fun getCommandId(parameter: String): CommandId {
        ensureParameterExists(parameter)
        return CommandId(delegate.getBigDecimal(parameter).toBigInteger())
    }

    override fun getBytes(parameter: String): ByteArray {
        ensureParameterExists(parameter)
        return delegate.getBytes(parameter)
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