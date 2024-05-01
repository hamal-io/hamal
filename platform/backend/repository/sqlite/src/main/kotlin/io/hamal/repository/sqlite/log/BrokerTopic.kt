package io.hamal.repository.sqlite.log

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.CreatedAt.Companion.CreatedAt
import io.hamal.lib.common.domain.UpdatedAt.Companion.UpdatedAt
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.log.LogBrokerRepository.CreateTopicCmd
import io.hamal.repository.api.log.LogBrokerRepository.LogTopicQuery
import io.hamal.repository.api.log.LogTopic
import java.nio.file.Path

internal class LogBrokerTopicSqliteRepository(
    internal val path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "log-broker-topic.db"
) {

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
         CREATE TABLE IF NOT EXISTS topics (
            id              INTEGER PRIMARY KEY,
            created_at      DATETIME NOT NULL,
            updated_at      DATETIME NOT NULL
        );
        """
            )
        }
    }

    fun create(cmd: CreateTopicCmd): LogTopic {
        return connection.execute<LogTopic>(
            """
            INSERT INTO topics(id, created_at, updated_at)
            VALUES (:id, :now, :now) RETURNING id, created_at, updated_at
            """
        ) {
            query {
                set("id", cmd.logTopicId)
                set("now", TimeUtils.now())
            }
            map { rs ->
                LogTopic(
                    id = rs.getId("id", ::LogTopicId),
                    createdAt = CreatedAt(rs.getInstant("created_at")),
                    updatedAt = UpdatedAt(rs.getInstant("updated_at")),
                )
            }
        }!!
    }

    fun find(id: LogTopicId): LogTopic? =
        connection.executeQueryOne("SELECT id, created_at, updated_at FROM topics WHERE id = :id") {
            query {
                set("id", id)
            }
            map { rs ->
                LogTopic(
                    id = rs.getId("id", ::LogTopicId),
                    createdAt = CreatedAt(rs.getInstant("created_at")),
                    updatedAt = UpdatedAt(rs.getInstant("updated_at"))
                )
            }
        }

    fun list(query: LogTopicQuery): List<LogTopic> {
        return connection.executeQuery<LogTopic>(
            """
                SELECT
                    id, created_at, updated_at
                FROM
                    topics
                WHERE
                    id < :afterId
                ORDER BY id DESC
                LIMIT :limit
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map { rs ->
                LogTopic(
                    id = rs.getId("id", ::LogTopicId),
                    createdAt = CreatedAt(rs.getInstant("created_at")),
                    updatedAt = UpdatedAt(rs.getInstant("updated_at"))
                )
            }
        }
    }

    fun count(query: LogTopicQuery): Count {
        return Count(connection.executeQueryOne(
            """
            SELECT
                COUNT(*) as count
            FROM
                topics
            WHERE
                id < :afterId
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
            }
            map {
                it.getLong("count")
            }
        } ?: 0L)
    }

    override fun clear() {
        connection.tx {
            execute("DELETE FROM topics")
        }
    }

    override fun close() {
        connection.close()
    }

}
