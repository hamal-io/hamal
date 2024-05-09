package io.hamal.repository.sqlite.record

import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.record.Record

interface ProjectionSqlite<ID : ValueVariableSnowflakeId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun upsert(tx: RecordTransactionSqlite<ID, RECORD, OBJ>, obj: OBJ)
    fun delete(tx: RecordTransactionSqlite<ID, RECORD, OBJ>, id: ID)
    fun setupSchema(connection: Connection)
    fun clear(tx: Transaction)

    abstract class CurrentImpl<ID : ValueVariableSnowflakeId, RECORD : Record<ID>, OBJ : DomainObject<ID>> : ProjectionSqlite<ID, RECORD, OBJ> {

        override fun delete(tx: RecordTransactionSqlite<ID, RECORD, OBJ>, id: ID) {
            tx.execute("""DELETE FROM current WHERE id = :id""") {
                set("id", id)
            }
        }

        override fun clear(tx: Transaction) {
            tx.execute("""DELETE FROM current""")
        }
    }

    abstract class UniqueImpl<ID : ValueVariableSnowflakeId, RECORD : Record<ID>, OBJ : DomainObject<ID>>(
        private val tableName: String
    ) : ProjectionSqlite<ID, RECORD, OBJ> {

        override fun delete(tx: RecordTransactionSqlite<ID, RECORD, OBJ>, id: ID) {
            tx.execute("""DELETE FROM $tableName WHERE id = :id""") {
                set("id", id)
            }
        }

        override fun clear(tx: Transaction) {
            tx.execute("""DELETE FROM $tableName""")
        }
    }
}