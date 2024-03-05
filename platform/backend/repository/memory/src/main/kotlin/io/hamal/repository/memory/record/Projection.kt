package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.ValueObjectId

interface ProjectionMemory<ID : ValueObjectId, OBJ : DomainObject<ID>> {
    fun upsert(obj: OBJ)
    fun clear()
}