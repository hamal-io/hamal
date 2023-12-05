package io.hamal.repository.sqlite

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.StateRepository
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.file.Path

class StateSqliteRepository(
    config: Config
) : SqliteBaseRepository(config), StateRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "state.db"
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

    override fun set(cmdId: CmdId, correlatedState: CorrelatedState) {
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
                set("funcId", correlatedState.correlation.funcId)
                set("correlationId", correlatedState.correlation.correlationId.value)
                set("value", protoBuf.encodeToByteArray(State.serializer(), correlatedState.value))
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
                set("correlationId", correlation.correlationId.value)
            }
            map { rs ->
                CorrelatedState(
                    correlation = Correlation(
                        funcId = rs.getDomainId("func_id", ::FuncId),
                        correlationId = CorrelationId(rs.getString("correlation_id"))
                    ),
                    value = protoBuf.decodeFromByteArray(State.serializer(), rs.getBytes("value"))
                )
            }
        }
    }

    private val protoBuf = ProtoBuf { }
}