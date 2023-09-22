package io.hamal.repository.api

interface Repository : AutoCloseable {
    fun clear()
}