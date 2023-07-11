package io.hamal.lib.sqlite

import io.hamal.lib.common.util.FileUtils
import io.hamal.lib.domain.Once
import logger
import java.io.Closeable
import java.nio.file.Path
import kotlin.io.path.Path

abstract class BaseSqliteRepository(
    val config: Config
) : Closeable {

    protected val log = logger(this::class)

    private val connectionOnce = Once.default<Connection>()

    val connection by lazy {
        connectionOnce {
            val result = DefaultConnection(
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

    abstract fun setupConnection(connection: Connection)
    abstract fun setupSchema(connection: Connection)
    abstract fun clear()

    override fun close() {
        if (connection.isOpen) {
            connection.close()
        }
    }


}

private fun ensureFilePath(config: BaseSqliteRepository.Config): Path {
    return FileUtils.createDirectories(config.path)
        .resolve(config.path.resolve(Path(config.filename)))
}

//FIXME properly integrate this
fun <T : Any> unsafeInCriteria(parameter: String, values: Iterable<T>): String {
    return "$parameter in (${values.joinToString(",") { it.toString() }})"
}