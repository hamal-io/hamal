package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.Chunk
import io.hamal.backend.repository.api.log.Segment
import io.hamal.backend.repository.api.log.SegmentRepository
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.core.Shard
import io.hamal.lib.core.util.TimeUtils
import java.nio.file.Path


internal class DefaultSegmentRepository(
    internal val segment: Segment,
) : BaseRepository(object : Config {
    override val path: Path get() = segment.path
    override val filename: String get() = String.format("%020d.db", segment.id.value.toLong())
    override val shard: Shard get() = segment.shard
}), SegmentRepository {

    override fun append(bytes: ByteArray): Chunk.Id {
        return connection.tx {
            execute<Chunk.Id>("INSERT INTO chunks (bytes,instant) VALUES (:bytes,:now) RETURNING id") {
                with {
                    set("bytes", bytes)
                    set("now", TimeUtils.now())
                }
                map { Chunk.Id(it.getInt("id")) }
            }
        }!!
    }

    override fun read(firstId: Chunk.Id, limit: Int): List<Chunk> {
        if (limit < 1) {
            return listOf()
        }
        return connection.executeQuery<Chunk>(
            """SELECT id, bytes, instant FROM chunks WHERE id >= :firstId LIMIT :limit """.trimIndent()
        ) {
            with {
                set("firstId", firstId)
                set("limit", limit)
            }
            map {
                Chunk(
                    id = it.getDomainId("id", Chunk::Id),
                    segmentId = segment.id,
                    partitionId = segment.partitionId,
                    topicId = segment.topicId,
                    bytes = it.getBytes("bytes"),
                    instant = it.getInstant("instant"),
                    shard = segment.shard
                )
            }
        }
    }

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.tx {
            execute(
                """
             CREATE TABLE IF NOT EXISTS chunks (
                id INTEGER PRIMARY KEY AUTOINCREMENT ,
                bytes BLOB NOT NULL ,
                instant DATETIME NOT NULL
            );
        """.trimIndent()
            )
        }
    }

    override fun clear() {
        connection.tx {
            execute("DELETE FROM chunks")
            execute("DELETE FROM sqlite_sequence")
        }
    }

    override fun close() {
        connection.close()
    }

    override fun count() = connection.executeQueryOne("SELECT COUNT(*) as count from chunks") {
        map {
            it.getLong("count").toULong()
        }
    } ?: 0UL
}