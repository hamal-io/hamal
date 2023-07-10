package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.CreateDomainObject
import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.sqlite.BaseRepository
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.sqlite.Connection
import kotlin.reflect.KClass


abstract class SqliteRecordRepository<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
    config: Config,
    private val createDomainObject: CreateDomainObject<ID, RECORD, OBJ>,
    private val recordClass: KClass<RECORD>,
    private val recordCache: RecordCache<ID, RECORD, OBJ> = RecordCache(
        RecordLoader(recordClass)
    ),
    private val projections: List<Projection<ID, RECORD, OBJ>>
) : BaseRepository(object : Config {
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
                 cmd_id         NUMERIC NOT NULL,
                 entity_id      INTEGER NOT NULL,
                 sequence       INTEGER NOT NULL DEFAULT 0,
                 data           BLOB NOT NULL,
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
        connection.execute("DELETE FROM records")
        projections.forEach { projection -> projection.clear(connection) }
    }

    fun <T> tx(block: RecordTransaction<ID, RECORD, OBJ>.() -> T): T {
        return try {
            connection.tx {
                block(
                    SqliteRecordTransaction(
                        createDomainObject,
                        recordClass,
                        this,
                        recordCache
                    )
                )
            }
        } catch (t: Throwable) {
            /**
             * Exception / errors during block execution lead to inconsistent data.
             * 2 Options -  Make the Cache transactional or wipe all cache data
             * As this should rarely happen --> simpler and cheaper execution for happy path
             */
            recordCache.invalidate()
            projections.forEach(Projection<ID, RECORD, OBJ>::invalidate)
            throw t
        }
    }

}