package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.LogChunk
import io.hamal.backend.repository.api.log.LogChunkId
import io.hamal.backend.repository.api.log.LogSegment
import io.hamal.backend.repository.api.log.LogSegmentRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import java.nio.file.Path

data class SqliteLogSegment(
    override val id: LogSegment.Id,
    override val topicId: TopicId,
    val path: Path
) : LogSegment


internal class SqliteLogSegmentRepository(
    internal val segment: SqliteLogSegment,
) : BaseSqliteRepository(object : Config {
    override val path: Path get() = segment.path
    override val filename: String get() = String.format("%020d.db", segment.id.value.toLong())
}), LogSegmentRepository {

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        connection.tx {
            execute("INSERT OR IGNORE INTO chunks (cmd_id,bytes,instant) VALUES (:cmdId, :bytes,:now)") {
                set("cmdId", cmdId)
                set("bytes", bytes)
                set("now", TimeUtils.now())
            }
        }
    }

    override fun read(firstId: LogChunkId, limit: Int): List<LogChunk> {
        if (limit < 1) {
            return listOf()
        }
        return connection.executeQuery<LogChunk>(
            """SELECT id, bytes, instant FROM chunks WHERE id >= :firstId LIMIT :limit """.trimIndent()
        ) {
            query {
                set("firstId", firstId)
                set("limit", limit)
            }
            map {
                LogChunk(
                    id = it.getDomainId("id", ::LogChunkId),
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