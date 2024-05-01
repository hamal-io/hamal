package io.hamal.repository.sqlite

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.StateCmdRepository
import io.hamal.repository.api.StateRepository
import io.hamal.repository.record.json
import java.nio.file.Path

class StateSqliteRepository(
    path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "state.db"
), StateRepository {

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
                CREATE TABLE IF NOT EXISTS states (
                    func_id INTEGER NOT NULL ,
                    correlation_id TEXT NOT NULL ,
                    value BLOB,
                    PRIMARY KEY (func_id, correlation_id)
               )
            """.trimIndent()
            )
        }
    }

    override fun set(cmd: StateCmdRepository.SetCmd) {
        connection.execute<Unit>(
            """
            INSERT INTO states (func_id, correlation_id, value)
                VALUES(:funcId, :correlationId, :value) 
                ON CONFLICT (func_id, correlation_id) 
                DO 
                    UPDATE SET value = EXCLUDED.value;
        """.trimIndent()
        ) {
            query {
                set("funcId", cmd.correlatedState.correlation.funcId)
                set("correlationId", cmd.correlatedState.correlation.id)
                set("value", json.serializeAndCompress(cmd.correlatedState.value))
            }
        }
    }

    override fun clear() {
        connection.execute("""DELETE FROM states""")
    }

    override fun find(correlation: Correlation): CorrelatedState? {
        return connection.executeQueryOne("SELECT func_id, correlation_id, value FROM states WHERE func_id = :funcId AND correlation_id = :correlationId") {
            query {
                set("funcId", correlation.funcId)
                set("correlationId", correlation.id)
            }
            map { rs ->
                CorrelatedState(
                    correlation = Correlation(
                        funcId = rs.getId("func_id", ::FuncId),
                        id = CorrelationId(rs.getString("correlation_id"))
                    ),
                    value = json.decompressAndDeserialize(State::class, rs.getBytes("value"))
                )
            }
        }
    }
}