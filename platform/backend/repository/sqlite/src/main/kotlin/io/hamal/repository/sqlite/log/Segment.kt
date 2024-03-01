package io.hamal.repository.sqlite.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.None
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.Limit.Companion.one
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.log.*
import java.nio.file.Path


data class LogSegmentSqlite(
    override val id: LogSegmentId,
    override val topicId: LogTopicId,
    val path: Path
) : LogSegment

class LogSegmentSqliteRepository(
    private val segment: LogSegmentSqlite
) : SqliteBaseRepository(
    path = segment.path,
    filename = String.format("%020d.db", segment.id.value.toLong())
), LogSegmentRepository {

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
             CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT ,
                cmd_id VARCHAR(255) NOT NULL UNIQUE,
                bytes BLOB NOT NULL ,
                instant DATETIME NOT NULL
            );
        """.trimIndent()
            )
        }
    }

    override fun append(cmdId: CmdId, bytes: ByteArray) {
        connection.tx {
            execute("INSERT OR IGNORE INTO events (cmd_id,bytes,instant) VALUES (:cmdId, :bytes,:now)") {
                set("cmdId", cmdId)
                set("bytes", bytes)
                set("now", TimeUtils.now())
            }
        }
    }

    override fun read(firstId: LogEventId, limit: Limit): List<LogEvent> {
        if (limit < one) {
            return listOf()
        }
        return connection.executeQuery<LogEvent>(
            """SELECT id, bytes, instant FROM events WHERE id >= :firstId LIMIT :limit """.trimIndent()
        ) {
            query {
                set("firstId", firstId.value)
                set("limit", limit)
            }
            map {
                LogEvent(
                    id = LogEventId(it.getSnowflakeId("id")),
                    segmentId = segment.id,
                    topicId = segment.topicId,
                    bytes = it.getBytes("bytes"),
                    instant = it.getInstant("instant")
                )
            }
        }
    }

    override fun count(): Count = connection.executeQueryOne("SELECT COUNT(*) as count from events") {
        map {
            Count(it.getLong("count"))
        }
    } ?: None


    override fun clear() {
        connection.tx {
            execute("DELETE FROM events")
            execute("DELETE FROM sqlite_sequence")
        }
    }

    override fun close() {
        connection.close()
    }
}