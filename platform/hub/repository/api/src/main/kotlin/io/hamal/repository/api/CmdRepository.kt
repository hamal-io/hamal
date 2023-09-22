package io.hamal.repository.api

interface CmdRepository : AutoCloseable {
    fun clear()
}