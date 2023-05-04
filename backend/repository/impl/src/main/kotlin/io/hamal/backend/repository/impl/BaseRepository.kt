package io.hamal.backend.repository.impl

import io.hamal.backend.core.port.logger
import io.hamal.backend.repository.impl.internal.Connection
import io.hamal.backend.repository.impl.internal.DefaultConnection
import io.hamal.lib.Once
import io.hamal.lib.Shard
import io.hamal.lib.util.Files
import java.nio.file.Path
import kotlin.io.path.Path

abstract class BaseRepository(
    val config: Config
) : AutoCloseable {

    protected val log = logger("${this::class.simpleName}-${config.shard}")

    private val connectionOnce = Once.default<Connection>()

    protected val connection: Connection
        get() = connectionOnce {
            val result = DefaultConnection(
                "${this::class.simpleName}-${config.shard}",
                "jdbc:sqlite:${ensureFilePath(config)}"
            )

            log.debug("Setup connection")
            setupConnection(result)
            log.debug("Setup schema")
            setupSchema(result)

            result
        }

//    by lazy {
//        DefaultConnection(
//            config.filename,
//            "jdbc:sqlite:${ensureFilePath(config)}"
//        )
//    }.also {
//        log.debug("Setup connection")
//        setupConnection()
//        log.debug("Setup schema")
//        setupSchema()
//    }

    interface Config {
        val path: Path
        val filename: String
        val shard: Shard
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

private fun ensureFilePath(config: BaseRepository.Config): Path {
    return Files.createDirectories(config.path)
        .resolve(Path(config.filename))
}