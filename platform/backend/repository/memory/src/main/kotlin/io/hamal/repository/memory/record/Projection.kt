package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.value.ValueVariableSnowflakeId

interface ProjectionMemory<ID : ValueVariableSnowflakeId, OBJ : DomainObject<ID>> {
    fun upsert(obj: OBJ)
    fun clear()
}