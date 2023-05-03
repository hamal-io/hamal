package io.hamal.backend.store.impl.internal

import io.hamal.lib.Shard
import io.hamal.lib.util.Files
import java.nio.file.Path
import kotlin.io.path.Path

abstract class BaseStore(config: Config) : AutoCloseable {

    protected val connection: Connection by lazy {
        val result = DefaultConnection("jdbc:sqlite:${ensureFilePath(config)}")
        setupSchema()
        setupConnection()
        result
    }

    interface Config {
        val path: Path
        val name: String
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

private fun ensureFilePath(config: BaseStore.Config): Path {
    return Files.createDirectories(config.path)
        .resolve(Path(String.format("${config.name}-%04d.db", config.shard.value.toLong())))
}