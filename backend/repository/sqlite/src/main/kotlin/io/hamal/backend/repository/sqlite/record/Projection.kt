package io.hamal.backend.repository.sqlite.record

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.sqlite.internal.Connection
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.base.DomainId

interface Projection<ID : DomainId, RECORD : Record<ID>, OBJ : DomainObject<ID>> {
    fun update(tx: RecordTransaction<ID, RECORD, OBJ>, obj: OBJ)
    fun setupSchema(connection: Connection)
    fun clear(connection: Connection)
    fun invalidate()
}