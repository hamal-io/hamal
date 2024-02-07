package io.hamal.lib.sqlite

import io.hamal.lib.common.logger
import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.Once
import java.nio.file.Path

abstract class SqliteBaseRepository(
    val config: Config
) : AutoCloseable {

    protected val log = logger(this::class)

    private val connectionOnce = Once.default<Connection>()

    val connection by lazy {
        connectionOnce {
            val result = ConnectionImpl(
                this::class,
                "jdbc:sqlite:${ensureFilePath(config)}"
            )
            log.debug("Setup connection")
            setupConnection(result)
            log.debug("Setup schema")
            setupSchema(result)
            result
        }
    }

    interface Config {
        val path: Path
        val filename: String
    }

    protected abstract fun setupConnection(connection: Connection)
    protected abstract fun setupSchema(connection: Connection)
    abstract fun clear()

    override fun close() {
        if (connection.isOpen) {
            connection.close()
        }
    }
}

private fun ensureFilePath(config: SqliteBaseRepository.Config): Path {
    return FileUtils.ensureFilePath(config.path, config.filename)
}

//FIXME properly integrate this
fun <T : Any> unsafeInCriteria(parameter: String, values: Iterable<T>): String {
    return "$parameter in (${values.joinToString(",") { it.toString() }})"
}