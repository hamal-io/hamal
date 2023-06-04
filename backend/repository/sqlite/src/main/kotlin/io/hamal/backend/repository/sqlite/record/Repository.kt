package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.api.domain.Exec
import io.hamal.backend.repository.api.record.exec.ExecRecord
import io.hamal.backend.repository.api.record.exec.createEntity
import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.backend.repository.sqlite.internal.Transaction
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.base.DomainId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf


abstract class SqliteRecordRepository<ID : DomainId, RECORD : Record<ID>>(
    config: Config
) : BaseRepository(config) {

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS records (
                 cmd_id         NUMERIC NOT NULL,
                 entity_id      INTEGER NOT NULL,
                 sequence       INTEGER NOT NULL,
                 data           BLOB NOT NULL,
                 PRIMARY KEY    (entity_id, sequence),
                 UNIQUE (cmd_id)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}

@OptIn(ExperimentalSerializationApi::class)
internal fun Transaction.recordsOf(execId: ExecId): List<ExecRecord> {
    return executeQuery(
        """
        SELECT 
            data
        FROM records 
            WHERE entity_id = :entityId 
        ORDER BY sequence;
    """.trimIndent()
    ) {
        query {
            set("entityId", execId)
        }
        map { rs ->
            ProtoBuf { }.decodeFromByteArray<ExecRecord>(rs.getBytes("data"))
        }
    }
}


internal fun Transaction.currentVersion(execId: ExecId): Exec {
    return recordsOf(execId).createEntity().toDomainObject()
}

internal fun Transaction.commandAlreadyApplied(execId: ExecId, cmdId: CmdId) =
    recordsOf(execId).any { it.cmdId == cmdId }

internal fun Transaction.versionOf(execId: ExecId, cmdId: CmdId): Exec {
    return recordsOf(execId).takeWhileInclusive { it.cmdId != cmdId }.createEntity().toDomainObject()
}