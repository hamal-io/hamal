package io.hamal.repository.sqlite.record

import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.Record
import kotlin.reflect.KClass


abstract class SqliteRecordRepository<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
    config: Config,
    private val createDomainObject: CreateDomainObject<ID, RECORD, OBJ>,
    private val recordClass: KClass<RECORD>,
    private val projections: List<SqliteProjection<ID, RECORD, OBJ>>

) : SqliteBaseRepository(object : Config {
    override val path = config.path
    override val filename = config.filename
}) {

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
                 cmd_id         VARCHAR(255) NOT NULL,
                 entity_id      INTEGER NOT NULL,
                 sequence       INTEGER NOT NULL DEFAULT 0,
                 data           BLOB NOT NULL,
                 timestamp      DEFAULT CURRENT_TIMESTAMP NOT NULL,
                 PRIMARY KEY    (entity_id, sequence),
                 UNIQUE         (entity_id, cmd_id)
            );
        """.trimIndent()
        )
        connection.execute(
            """
             CREATE TRIGGER IF NOT EXISTS auto_sequence
                AFTER INSERT ON records
                WHEN new.sequence = 0
                BEGIN
                    UPDATE records
                    SET sequence = (SELECT IFNULL(MAX(sequence), 1) + 1 FROM records WHERE entity_id = new.entity_id)
                    WHERE entity_id = new.entity_id AND sequence = new.sequence;
                END;
        """.trimIndent()
        )

        projections.forEach { projection -> projection.setupSchema(connection) }

    }

    override fun clear() {
        connection.tx {
            projections.forEach { projection -> projection.clear(this) }
            execute("DELETE FROM records")
        }
    }

    fun <T> tx(block: SqliteRecordTransaction<ID, RECORD, OBJ>.() -> T): T {
        return connection.tx {
            block(
                SqliteRecordTransaction(
                    createDomainObject,
                    recordClass,
                    this,
                )
            )
        }
    }
}