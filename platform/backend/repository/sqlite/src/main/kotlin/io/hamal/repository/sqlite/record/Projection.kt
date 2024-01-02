package io.hamal.repository.sqlite.record

import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.record.Record

interface ProjectionSqlite<ID : ValueObjectId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun upsert(tx: RecordTransactionSqlite<ID, RECORD, OBJ>, obj: OBJ)
    fun setupSchema(connection: Connection)
    fun clear(tx: Transaction)
}