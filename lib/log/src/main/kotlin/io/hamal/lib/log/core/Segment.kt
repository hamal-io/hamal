package io.hamal.lib.log.core

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
    internal val lock: Lock,
    internal val baseId: Id,
    internal val connection: Connection
) : AutoCloseable {

    companion object {
        fun open(config: Config): Segment {
            val dbPath = ensureDirectoryExists(config)
            return Segment(
                lock = ReentrantLock(),
                baseId = config.id,
                connection = DriverManager.getConnection("jdbc:sqlite:$dbPath.db")
            )
        }

        private fun ensureDirectoryExists(config: Config): Path {
            return Files.createDirectories(config.path)
                .resolve(Path(java.lang.String.format("%020d", config.id.value)))
        }
    }

    init {
        setupSqlite()
        connection.autoCommit = false
        setupSchema()
    }

    @JvmInline
    value class Id(val value: Long) {
        constructor(value: Int) : this(value.toLong())
    }

    data class Config(
        val id: Id,
        val path: Path,
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

    fun countRecords(): Long = this.executeQuery("SELECT COUNT(*) from records") { it.getLong(1) }

    fun append(vararg toRecord: ToRecord): List<Id> {
        return append(toRecord.toList())
    }

    fun append(toRecord: Collection<ToRecord>): List<Id> {
        if (toRecord.isEmpty()) {
            return listOf()
        }

        return lock.withLock {
            connection.beginRequest()
            val beforeLastRowId = countRecords() + 1

            val stmt = connection.prepareStatement(
                """INSERT INTO records (key, value,instant) VALUES (?,?,?);""".trimIndent(),
                Statement.RETURN_GENERATED_KEYS
            )
            toRecord.forEach { record ->
                stmt.setBytes(1, record.key.array())
                stmt.setBytes(2, record.value.array())
                stmt.setTimestamp(3, Timestamp.from(record.instant))
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

