package io.hamal.lib.log.core

import java.lang.String.format
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.io.path.Path


class Segment(
    config: Config
) : AutoCloseable {

    internal val connection: Connection

    init {
        val dbPath = config.path.resolve(Path(format("%020d", config.id.value.toLong())))
        connection = DriverManager.getConnection("jdbc:sqlite:$dbPath.db")
        setupSchema()
        setupSqlite()
    }

    data class Config(
        val path: Path,
        val id: Id
    )

    companion object {
        fun open(config: Config): Segment {
            ensureDirectoryExists(config)
            return Segment(config)
        }

        private fun ensureDirectoryExists(config: Config) {
            Files.createDirectories(config.path)
        }
    }

    override fun close() {
        connection.close()
    }

    @JvmInline
    value class Id(val value: Long) {
        constructor(value: Int) : this(value.toLong())
    }
}

internal fun <T> Segment.executeQuery(sql: String, fn: (ResultSet) -> T) {
    require(!connection.isClosed) { "Connection must be open" }
    connection.createStatement().use { statement ->
        statement.executeQuery(sql).use(fn)
    }
}

private fun Segment.setupSchema() {
    connection.createStatement().use {
        it.execute(
            """
         CREATE TABLE IF NOT EXISTS records (
            id INTEGER PRIMARY KEY AUTOINCREMENT ,
            key BLOB NOT NULL ,
            value BLOB NOT NULL ,
            instant DATETIME NOT NULL
        );
        """.trimIndent()
        )
    }
}

private fun Segment.setupSqlite() {
    connection.createStatement().use {
        it.execute("""PRAGMA journal_mode = wal;""")
        it.execute("""PRAGMA locking_mode = exclusive;""")
        it.execute("""PRAGMA temp_store = memory;""")
        it.execute("""PRAGMA synchronous = off;""")
    }
}

