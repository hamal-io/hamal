package io.hamal.repository.sqlite

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.*
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
    config: Config
) : SqliteBaseRepository(config), ExecLogRepository {
    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "exec_log.db"
    }

    override fun setupSchema(connection: Connection) {
        connection.tx {
            execute(
                """
                CREATE TABLE IF NOT EXISTS exec_log (
                    id INTEGER NOT NULL,
                    exec_id INTEGER NOT NULL,
                    group_id INTEGER NOT NULL,
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
                (id, exec_id, group_id, message, level, timestamp)
            VALUES
                (:id, :exec_id, :group_id, :message, :level, :timestamp) 
            RETURNING *;
        """.trimIndent()
        ) {
            query {
                set("id", cmd.execLogId)
                set("exec_id", cmd.execId)
                set("group_id", cmd.groupId)
                set("message", cmd.message.value)
                set("level", cmd.level.value)
                set("timestamp", cmd.timestamp.value.toEpochMilli())
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
                ${query.groupIds()}
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

    override fun count(query: ExecLogQuery): ULong {
        return connection.executeQueryOne(
            """
            SELECT 
                COUNT(*) as count 
            FROM 
                exec_log
            WHERE
                id < :afterId
                ${query.groupIds()}
                ${query.ids()}
                ${query.execIds()}
            """.trimIndent()
        ) {
            query {
                set("afterId", query.afterId)
            }
            map {
                it.getLong("count").toULong()
            }
        } ?: 0UL
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
        "AND id IN (${execLogIds.joinToString(",") { "${it.value.value}" }})"
    }
}

private fun ExecLogQuery.groupIds(): String {
    return if (groupIds.isEmpty()) {
        ""
    } else {
        "AND group_id IN (${groupIds.joinToString(",") { "${it.value.value}" }})"
    }
}

private fun ExecLogQuery.execIds(): String {
    return if (execIds.isEmpty()) {
        ""
    } else {
        "AND exec_id IN (${execIds.joinToString(",") { "${it.value.value}" }})"
    }
}

private fun NamedResultSet.toExecLog(): ExecLog {
    return ExecLog(
        id = getId("id", ::ExecLogId),
        execId = getId("exec_id", ::ExecId),
        groupId = getId("group_id", ::GroupId),
        level = ExecLogLevel.of(getInt("level")),
        message = ExecLogMessage(getString("message")),
        timestamp = ExecLogTimestamp(Instant.ofEpochMilli(getLong("timestamp")))
    )
}

