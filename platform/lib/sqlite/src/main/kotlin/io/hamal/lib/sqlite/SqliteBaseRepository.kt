package io.hamal.lib.sqlite

import io.hamal.lib.common.logger
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.Once
import java.nio.file.Path

abstract class SqliteBaseRepository(
    private val path: Path,
    private val filename: String
) : AutoCloseable {


    protected abstract fun setupConnection(connection: Connection)
    protected abstract fun setupSchema(connection: Connection)
    abstract fun clear()

    override fun close() {
        if (connection.isOpen) {
            connection.close()
        }
    }

    protected val log = logger(this::class)

    private val connectionOnce = Once.default<Connection>()

    val connection by lazy {
        connectionOnce {
            val result = ConnectionImpl(
                this::class,
                "jdbc:sqlite:${ensureFilePath(path, filename)}"
            )
            log.debug("Setup connection")
            setupConnection(result)
            log.debug("Setup schema")
            setupSchema(result)
            result
        }
    }
}

private fun ensureFilePath(path: Path, filename: String): Path {
    return FileUtils.ensureFilePath(path, filename)
}