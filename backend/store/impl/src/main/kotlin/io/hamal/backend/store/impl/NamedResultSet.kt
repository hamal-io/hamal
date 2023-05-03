package io.hamal.backend.store.impl

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
    override fun getBoolean(parameter: String) = delegate.getBoolean(parameter)
    override fun getInt(parameter: String) = delegate.getInt(parameter)
    override fun getLong(parameter: String) = delegate.getLong(parameter)
    override fun getString(parameter: String): String = delegate.getString(parameter)
    override fun getInstant(parameter: String): Instant = delegate.getTimestamp(parameter).toInstant()
    override fun getSnowflakeId(parameter: String): SnowflakeId = SnowflakeId(delegate.getLong(parameter))
    override fun <DOMAIN_ID : DomainId> getDomainId(parameter: String, ctor: (SnowflakeId) -> DOMAIN_ID): DomainId {
        return ctor(getSnowflakeId(parameter))
    }

    override fun getRequestId(parameter: String): RequestId {
        return RequestId(delegate.getBigDecimal(parameter).toBigInteger())
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