package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogChunk
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.backend.repository.api.log.LogSegmentRepository
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.common.Shard
import io.hamal.lib.common.util.TimeUtils
import java.nio.file.Path


internal class DefaultLogSegmentRepository(
    internal val segment: LogSegment,
) : BaseRepository(object : Config {
    override val path: Path get() = segment.path
    override val filename: String get() = String.format("%020d.db", segment.id.value.toLong())
    override val shard: Shard get() = segment.shard
}), LogSegmentRepository {

    override fun append(bytes: ByteArray): LogChunk.Id {
        return connection.tx {
            execute<LogChunk.Id>("INSERT INTO chunks (bytes,instant) VALUES (:bytes,:now) RETURNING id") {
                with {
                    set("bytes", bytes)
                    set("now", TimeUtils.now())
                }
                map { LogChunk.Id(it.getInt("id")) }
            }
        }!!
    }

    override fun read(firstId: LogChunk.Id, limit: Int): List<LogChunk> {
        if (limit < 1) {
            return listOf()
        }
        return connection.executeQuery<LogChunk>(
            """SELECT id, bytes, instant FROM chunks WHERE id >= :firstId LIMIT :limit """.trimIndent()
        ) {
            with {
                set("firstId", firstId)
                set("limit", limit)
            }
            map {
                LogChunk(
                    id = it.getDomainId("id", LogChunk::Id),
                    segmentId = segment.id,
                    shard = segment.shard,
                    topicId = segment.topicId,
                    bytes = it.getBytes("bytes"),
                    instant = it.getInstant("instant")
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