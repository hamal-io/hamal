package io.hamal.backend.store.impl

import java.sql.ResultSet

interface NamedResultSet<T : Any> : AutoCloseable {
    fun getBoolean(parameter: String): Boolean
    fun getInt(parameter: String): Int
    fun map(block: (NamedResultSet<T>) -> T): List<T>
}


class DefaultNamedResultSet<T : Any>(
    internal val delegate: ResultSet
) : NamedResultSet<T> {
    override fun getBoolean(parameter: String) = delegate.getBoolean(parameter)

    override fun getInt(parameter: String) = delegate.getInt(parameter)

    override fun map(fn: (NamedResultSet<T>) -> T): List<T> {
        val result = mutableListOf<T>()
        while (delegate.next()) {
            result.add(fn(this))
        }
        return result
    }

    override fun close() {
        delegate.close()
    }

}