package io.hamal.backend.repository.impl.log

import io.hamal.backend.repository.api.log.Chunk
import io.hamal.backend.repository.api.log.Segment
import io.hamal.backend.repository.api.log.SegmentRepository
import io.hamal.backend.repository.impl.BaseRepository
import io.hamal.lib.Shard
import java.nio.file.Path
import java.sql.ResultSet


internal class DefaultSegmentRepository(
    internal val segment: Segment,
) : BaseRepository(object : Config {
    override val path: Path get() = segment.path
    override val filename: String get() = String.format("%020d.db", segment.id.value.toLong())
    override val shard: Shard get() = segment.shard

}), SegmentRepository {

    override fun append(vararg bytes: ByteArray): List<Chunk.Id> {
        TODO()
//        if (bytes.isEmpty()) {
//            return listOf()
//        }
//
//        val now = TimeUtils.now()
//        val lastId = lock.withLock {
//            connection.prepareStatement("""INSERT INTO chunks (bytes,instant) VALUES (?,?);""".trimIndent())
//                .use { stmt ->
//                    bytes.forEach { bs ->
//                        stmt.setBytes(1, bs)
//                        stmt.setTimestamp(2, Timestamp.from(now))
//                        stmt.addBatch()
//                    }
//                    stmt.executeBatch()
//                    stmt.generatedKeys.getLong(1)
//                }.let {
//                    connection.commit()
//                    it
//                }
//        }
//
//        return LongRange(lastId - bytes.size + 1, lastId)
//            .map { Chunk.Id(it.toULong()) }
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        TODO()
//        if (limit < 1) {
//            return listOf()
//        }
//        return executeQuery(
//            """SELECT id, bytes, instant FROM chunks WHERE id >= ${firstId.value} LIMIT $limit """.trimIndent()
//        ) {
//            val result = mutableListOf<Chunk>()
//            while (it.next()) {
//                result.add(
//                    Chunk(
//                        id = Chunk.Id(it.getLong("id").toULong()),
//                        segmentId = segment.id,
//                        partitionId = segment.partitionId,
//                        topicId = segment.topicId,
//                        bytes = it.getBytes("bytes"),
//                        instant = it.getTimestamp("instant").toInstant()
//                    )
//                )
//            }
//            result
//        }
    }

    override fun setupConnection() {
    }

    override fun setupSchema() {
    }

    override fun close() {
        connection.close()
    }

    override fun count() = this.executeQuery("SELECT COUNT(*) from chunks") { it.getLong(1).toULong() }

}

private fun DefaultSegmentRepository.setupSchema() {
//    lock.withLock {
//        connection.createStatement().use {
//            it.execute(
//                """
//         CREATE TABLE IF NOT EXISTS chunks (
//            id INTEGER PRIMARY KEY AUTOINCREMENT ,
//            bytes BLOB NOT NULL ,
//            instant DATETIME NOT NULL
//        );
//        """.trimIndent()
//            )
//        }
//        connection.commit()
//    }
    TODO()
}

internal fun DefaultSegmentRepository.clear() {
//    lock.withLock {
//        connection.createStatement().use {
//            it.execute("DELETE FROM chunks")
//            it.execute("DELETE FROM sqlite_sequence")
//        }
//        connection.commit()
//    }
    TODO()
}


private fun DefaultSegmentRepository.setupSqlite() {
//    connection.createStatement().use {
//        it.execute("""PRAGMA journal_mode = wal;""")
//        it.execute("""PRAGMA locking_mode = exclusive;""")
//        it.execute("""PRAGMA temp_store = memory;""")
//        it.execute("""PRAGMA synchronous = off;""")
//    }
    TODO()
}

internal fun <T> DefaultSegmentRepository.executeQuery(sql: String, fn: (ResultSet) -> T): T {
//    require(!connection.isClosed) { "Connection must be open" }
//    return connection.createStatement().use { statement ->
//        statement.executeQuery(sql).use(fn)
//    }
    TODO()
}