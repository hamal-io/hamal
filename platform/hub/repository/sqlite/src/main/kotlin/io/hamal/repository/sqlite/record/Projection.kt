package io.hamal.repository.sqlite.record

import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.Transaction
import io.hamal.repository.record.Record

interface Projection<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun upsert(tx: RecordTransaction<ID, RECORD, OBJ>, obj: OBJ)
    fun setupSchema(connection: Connection)
    fun clear(tx: Transaction)
    fun invalidate()
}