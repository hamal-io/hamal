package io.hamal.repository.sqlite

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.domain.vo.ExecLogMessage.Companion.ExecLogMessage
import io.hamal.lib.domain.vo.ExecLogTimestamp.Companion.ExecLogTimestamp
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.NamedResultSet
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository.AppendCmd
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import io.hamal.repository.api.ExecLogRepository
import java.nio.file.Path
import java.time.Instant

class ExecLogSqliteRepository(
    path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "exec_log.db"
), ExecLogRepository {

    override fun setupSchema(connection: Connection) {
        connection.tx {
            execute(
                """
                CREATE TABLE IF NOT EXISTS exec_log (
                    id INTEGER NOT NULL,
                    exec_id INTEGER NOT NULL,
                    workspace_id INTEGER NOT NULL,
                    level INTEGER NOT NULL,
                    message VARCHAR(255) NOT NULL,
                    timestamp INTEGER NOT NULL,
                    PRIMARY KEY (id)
                    
                );
            """.trimIndent()
            )
        }
    }

    override fun append(cmd: AppendCmd): ExecLog {
        return connection.execute<ExecLog>(
            """
            INSERT OR REPLACE INTO exec_log 
                (id, exec_id, workspace_id, message, level, timestamp)
            VALUES
                (:id, :exec_id, :workspace_id, :message, :level, :timestamp) 
            RETURNING *;
        """.trimIndent()
        ) {
            query {
                set("id", cmd.execLogId)
                set("exec_id", cmd.execId)
                set("workspace_id", cmd.workspaceId)
                set("message", cmd.message)
                set("level", cmd.level.value)
                set("timestamp", cmd.timestamp.instantValue.toEpochMilli())
            }
            map(NamedResultSet::toExecLog)
        }!!
    }


    override fun list(query: ExecLogQuery): List<ExecLog> {
        return connection.executeQuery<ExecLog>(
            """
            SELECT 
                *
             FROM
                exec_log
            WHERE
                id < :afterId
                ${query.workspaceIds()}
                ${query.ids()}
                ${query.execIds()}
            ORDER BY id DESC
            LIMIT :limit
        """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
                set("limit", query.limit)
            }
            map(NamedResultSet::toExecLog)
        }
    }

    override fun count(query: ExecLogQuery): Count {
        return Count(
            connection.executeQueryOne(
                """
            SELECT 
                COUNT(*) as count 
            FROM 
                exec_log
            WHERE
                id < :afterId
                ${query.workspaceIds()}
                ${query.ids()}
                ${query.execIds()}
            """.trimIndent()
            ) {
                query {
                    set("afterId", query.afterId)
                }
                map {
                    it.getLong("count")
                }
            } ?: 0L
        )
    }

    override fun clear() {
        connection.tx {
            execute("DELETE FROM exec_log")
        }
    }


    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }


    override fun close() {}
}

private fun ExecLogQuery.ids(): String {
    return if (execLogIds.isEmpty()) {
        ""
    } else {
        "AND id IN (${execLogIds.joinToString(",") { "${it.longValue}" }})"
    }
}

private fun ExecLogQuery.workspaceIds(): String {
    return if (workspaceIds.isEmpty()) {
        ""
    } else {
        "AND workspace_id IN (${workspaceIds.joinToString(",") { "${it.longValue}" }})"
    }
}

private fun ExecLogQuery.execIds(): String {
    return if (execIds.isEmpty()) {
        ""
    } else {
        "AND exec_id IN (${execIds.joinToString(",") { "${it.longValue}" }})"
    }
}

private fun NamedResultSet.toExecLog(): ExecLog {
    return ExecLog(
        id = getId("id", ::ExecLogId),
        execId = getId("exec_id", ::ExecId),
        workspaceId = getId("workspace_id", ::WorkspaceId),
        level = ExecLogLevel.of(getInt("level")),
        message = ExecLogMessage(getString("message")),
        timestamp = ExecLogTimestamp(Instant.ofEpochMilli(getLong("timestamp")))
    )
}

