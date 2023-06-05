package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.domain.vo.base.DomainId
import kotlin.reflect.KClass


abstract class SqliteRecordRepository<ID : DomainId, RECORD : Record<ID>>(
    config: Config,
    private val recordClass: KClass<RECORD>
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
        connection.execute("DELETE FROM records")
    }

    fun <T> tx(block: RecordTransaction<ID, RECORD>.() -> T): T {
        return connection.tx {
            block(SqliteRecordTransaction(recordClass, this))
        }
    }

}