package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.Record
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.sqlite.Connection

interface Projection<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun update(tx: RecordTransaction<ID, RECORD, OBJ>, obj: OBJ)
    fun setupSchema(connection: Connection)
    fun clear(connection: Connection)
    fun invalidate()
}