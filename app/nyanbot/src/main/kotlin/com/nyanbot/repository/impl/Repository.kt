package com.nyanbot.repository.impl

import com.nyanbot.repository.record.CreateDomainObject
import com.nyanbot.repository.record.Record
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import java.nio.file.Path
import kotlin.reflect.KClass


abstract class RecordSqliteRepository<ID : ValueObjectId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
    path: Path,
    filename: String,
    private val createDomainObject: CreateDomainObject<ID, RECORD, OBJ>,
    private val recordClass: KClass<RECORD>,
    private val projections: List<ProjectionSqlite<ID, RECORD, OBJ>>

) : SqliteBaseRepository(path, filename) {

    override fun setupConnection(connection: Connection) {
//        connection.execute("""PRAGMA journal_mode = wal;""")
//        connection.execute("""PRAGMA locking_mode = exclusive;""")
//        connection.execute("""PRAGMA temp_store = memory;""")
//        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS records (
                 entity_id      INTEGER NOT NULL,
                 sequence       INTEGER NOT NULL DEFAULT 0,
                 data           BLOB NOT NULL,
                 timestamp      DEFAULT CURRENT_TIMESTAMP NOT NULL,
                 PRIMARY KEY    (entity_id, sequence)
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

    fun <T> tx(block: RecordTransactionSqlite<ID, RECORD, OBJ>.() -> T): T {
        return connection.tx {
            block(
                RecordTransactionSqlite(
                    createDomainObject,
                    recordClass,
                    this,
                )
            )
        }
    }
}