package io.hamal.lib.log.core

import io.hamal.lib.meta.Tuple3
import java.lang.String.format
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.sql.*
import java.time.Instant
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.Path


class Segment(
    config: Config,
    internal val lock: Lock = ReentrantLock()
) : AutoCloseable {

    companion object {
        fun open(config: Config): Segment {
            ensureDirectoryExists(config)
            return Segment(config)
        }

        private fun ensureDirectoryExists(config: Config) {
            Files.createDirectories(config.path)
        }
    }

    internal val connection: Connection

    init {
        val dbPath = config.path.resolve(Path(format("%020d", config.id.value.toLong())))
        connection = DriverManager.getConnection("jdbc:sqlite:$dbPath.db")
        setupSqlite()
        connection.autoCommit = false
        setupSchema()
    }

    data class Config(
        val path: Path,
        val id: Id
    )

    data class Record(
        val id: Id,
        val key: ByteBuffer,
        val value: ByteBuffer,
        val instant: Instant
    ) {
        @JvmInline
        value class Id(val value: Long) {
            constructor(value: Int) : this(value.toLong())
        }
    }

    fun countRows(): Long = this.executeQuery("SELECT COUNT(*) from records") { it.getLong(1) }

    fun append(vararg records: Tuple3<ByteBuffer, ByteBuffer, Instant>): List<Id> {
        return append(records.toList())
    }

    fun append(records: Collection<Tuple3<ByteBuffer, ByteBuffer, Instant>>): List<Id> {
        if (records.isEmpty()) {
            return listOf()
        }

        return lock.withLock {
            connection.beginRequest()
            val beforeLastRowId = countRows() + 1

            val stmt = connection.prepareStatement(
                """INSERT INTO records (key, value,instant) VALUES (?,?,?);""".trimIndent(),
                Statement.RETURN_GENERATED_KEYS
            )
            records.forEach { record ->
                stmt.setBytes(1, record._1.array())
                stmt.setBytes(2, record._2.array())
                stmt.setTimestamp(3, Timestamp.from(record._3))
                stmt.addBatch()
            }

            stmt.executeBatch()
            connection.commit()
            LongRange(beforeLastRowId, stmt.generatedKeys.getLong(1))
        }.map { Id(it) }
    }

    fun read(firstId: Id, limit: Int = 1): List<Record> {
        if (limit < 1) {
            return listOf()
        }
        return executeQuery(
            """SELECT id, key, value, instant FROM records WHERE id >= ${firstId.value} LIMIT $limit """.trimIndent()
        ) {
            val result = mutableListOf<Record>()
            while (it.next()) {
                result.add(
                    Record(
                        id = Record.Id(it.getLong("id")),
                        key = ByteBuffer.wrap(it.getBytes("key")),
                        value = ByteBuffer.wrap(it.getBytes("value")),
                        instant = it.getTimestamp("instant").toInstant()
                    )
                )
            }
            result
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

internal fun <T> Segment.executeQuery(sql: String, fn: (ResultSet) -> T): T {
    require(!connection.isClosed) { "Connection must be open" }
    return connection.createStatement().use { statement ->
        statement.executeQuery(sql).use(fn)
    }
}

internal fun Segment.clear() {
    lock.withLock {
        connection.beginRequest()
        connection.createStatement().use {
            it.execute("DELETE FROM records")
            it.execute("DELETE FROM sqlite_sequence")
        }
        connection.commit()
    }
}

private fun Segment.setupSchema() {
    connection.beginRequest()
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
    connection.commit()
}

private fun Segment.setupSqlite() {
    connection.createStatement().use {
        it.execute("""PRAGMA journal_mode = wal;""")
        it.execute("""PRAGMA locking_mode = exclusive;""")
        it.execute("""PRAGMA temp_store = memory;""")
        it.execute("""PRAGMA synchronous = off;""")
    }
}

