package io.hamal.repository.sqlite.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.repository.api.log.Chunk
import io.hamal.repository.api.log.ChunkId
import io.hamal.repository.api.log.Segment
import io.hamal.repository.api.log.SegmentRepository
import java.nio.file.Path

data class SqliteSegment(
    override val id: Segment.Id,
    override val topicId: TopicId,
    val path: Path
) : Segment


class SqliteSegmentRepository(
    internal val segment: SqliteSegment,
) : BaseSqliteRepository(object : Config {
    override val path: Path get() = segment.path
    override val filename: String get() = String.format("%020d.db", segment.id.value.toLong())
}), SegmentRepository {

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        connection.tx {
            execute("INSERT OR IGNORE INTO chunks (cmd_id,bytes,instant) VALUES (:cmdId, :bytes,:now)") {
                set("cmdId", cmdId)
                set("bytes", bytes)
                set("now", TimeUtils.now())
            }
        }
    }

    override fun read(firstId: ChunkId, limit: Int): List<Chunk> {
        if (limit < 1) {
            return listOf()
        }
        return connection.executeQuery<Chunk>(
            """SELECT id, bytes, instant FROM chunks WHERE id >= :firstId LIMIT :limit """.trimIndent()
        ) {
            query {
                set("firstId", firstId)
                set("limit", limit)
            }
            map {
                Chunk(
                    id = it.getDomainId("id", ::ChunkId),
                    segmentId = segment.id,
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
                cmd_id VARCHAR(255) NOT NULL UNIQUE,
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