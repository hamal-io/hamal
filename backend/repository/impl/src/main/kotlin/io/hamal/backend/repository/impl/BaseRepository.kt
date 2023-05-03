package io.hamal.backend.repository.impl

import io.hamal.backend.core.port.logger
import io.hamal.backend.repository.impl.internal.Connection
import io.hamal.backend.repository.impl.internal.DefaultConnection
import io.hamal.lib.Shard
import io.hamal.lib.util.Files
import java.nio.file.Path
import kotlin.io.path.Path

abstract class BaseRepository(config: Config) : AutoCloseable {

    protected val log = logger("${this::class.simpleName}-${config.shard}")

    protected val connection: Connection by lazy {
        val result = DefaultConnection(
            config.filename,
            "jdbc:sqlite:${ensureFilePath(config)}"
        )
        log.debug("Setup connection")
        setupConnection()
        log.debug("Setup schema")
        setupSchema()
        result
    }

    interface Config {
        val path: Path
        val filename: String
        val shard: Shard
    }

    abstract fun setupConnection()
    abstract fun setupSchema()

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