package io.hamal.repository.sqlite

import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository
import io.hamal.repository.api.ExecLogQueryRepository
import io.hamal.repository.api.ExecLogRepository
import java.nio.file.Path

class ExecLog(
    config: Config
) : SqliteBaseRepository(config), ExecLogRepository {
    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "execlog.db"
    }

    override fun append(cmd: ExecLogCmdRepository.AppendCmd): ExecLog {
        TODO("Not yet implemented")
    }

    override fun list(query: ExecLogQueryRepository.ExecLogQuery): List<ExecLog> {
        TODO("Not yet implemented")
    }

    override fun count(query: ExecLogQueryRepository.ExecLogQuery): ULong {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun setupConnection(connection: Connection) {
        TODO("Not yet implemented")
    }

    override fun setupSchema(connection: Connection) {
        connection.tx {
            execute(
                """
                CREATE TABLE IF NOT EXISTS auth (
                    id INTEGER NOT NULL,
                    exec_id INTEGER NOT NULL,
                    group_id INTEGER NOT NULL,
                    level INTEGER NOT NULL,
                    timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
                    PRIMARY KEY (id)
               )
            """.trimIndent()
            )
        }
    }

    override fun close() {}
}