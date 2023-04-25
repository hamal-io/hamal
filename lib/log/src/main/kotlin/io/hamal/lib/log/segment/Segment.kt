package io.hamal.lib.log.segment

import io.hamal.lib.log.partition.Partition
import io.hamal.lib.log.topic.Topic
import io.hamal.lib.util.Files
import io.hamal.lib.util.TimeUtils
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.Path

data class Segment(
    val id: Id,
    val partitionId: Partition.Id,
    val topicId: Topic.Id,
    val path: Path
) {
    @JvmInline
    value class Id(val value: ULong) {
        constructor(value: Int) : this(value.toULong())
    }
}

internal class SegmentRepository private constructor(
    internal val segment: Segment,
    internal val lock: Lock,
    internal val connection: Connection
) : ChunkAppender, ChunkReader, ChunkCounter, AutoCloseable {

    companion object {
        fun open(segment: Segment): SegmentRepository {
            val dbPath = ensureDirectoryExists(segment)
            val result = SegmentRepository(
                segment = segment,
                lock = ReentrantLock(),
                connection = DriverManager.getConnection("jdbc:sqlite:$dbPath.db")
            )

            result.setupSqlite()
            result.connection.autoCommit = false
            result.setupSchema()
            return result
        }

        private fun ensureDirectoryExists(segment: Segment): Path {
            return Files.createDirectories(segment.path)
                .resolve(Path(String.format("%020d", segment.id.value.toLong())))
        }
    }

    override fun append(vararg bytes: ByteArray): List<Chunk.Id> {
        if (bytes.isEmpty()) {
            return listOf()
        }

        val now = TimeUtils.now()
        val lastId = lock.withLock {
            connection.prepareStatement("""INSERT INTO chunks (bytes,instant) VALUES (?,?);""".trimIndent())
                .use { stmt ->
                    bytes.forEach { bs ->
                        stmt.setBytes(1, bs)
                        stmt.setTimestamp(2, Timestamp.from(now))
                        stmt.addBatch()
                    }
                    stmt.executeBatch()
                    stmt.generatedKeys.getLong(1)
                }.let {
                    connection.commit()
                    it
                }
        }

        return LongRange(lastId - bytes.size + 1, lastId)
            .map { Chunk.Id(it.toULong()) }
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        if (limit < 1) {
            return listOf()
        }
        return executeQuery(
            """SELECT id, bytes, instant FROM chunks WHERE id >= ${firstId.value} LIMIT $limit """.trimIndent()
        ) {
            val result = mutableListOf<Chunk>()
            while (it.next()) {
                result.add(
                    Chunk(
                        id = Chunk.Id(it.getLong("id").toULong()),
                        segmentId = segment.id,
                        partitionId = segment.partitionId,
                        topicId = segment.topicId,
                        bytes = it.getBytes("bytes"),
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

    override fun count() = this.executeQuery("SELECT COUNT(*) from chunks") { it.getLong(1).toULong() }

}

private fun SegmentRepository.setupSchema() {
    lock.withLock {
        connection.createStatement().use {
            it.execute(
                """
         CREATE TABLE IF NOT EXISTS chunks (
            id INTEGER PRIMARY KEY AUTOINCREMENT ,
            bytes BLOB NOT NULL ,
            instant DATETIME NOT NULL
        );
        """.trimIndent()
            )
        }
        connection.commit()
    }
}

internal fun SegmentRepository.clear() {
    lock.withLock {
        connection.createStatement().use {
            it.execute("DELETE FROM chunks")
            it.execute("DELETE FROM sqlite_sequence")
        }
        connection.commit()
    }
}


private fun SegmentRepository.setupSqlite() {
    connection.createStatement().use {
        it.execute("""PRAGMA journal_mode = wal;""")
        it.execute("""PRAGMA locking_mode = exclusive;""")
        it.execute("""PRAGMA temp_store = memory;""")
        it.execute("""PRAGMA synchronous = off;""")
    }
}

internal fun <T> SegmentRepository.executeQuery(sql: String, fn: (ResultSet) -> T): T {
    require(!connection.isClosed) { "Connection must be open" }
    return connection.createStatement().use { statement ->
        statement.executeQuery(sql).use(fn)
    }
}