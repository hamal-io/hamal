package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.value.ValueVariableSnowflakeId

interface ProjectionMemory<ID : ValueVariableSnowflakeId, OBJ : DomainObject<ID>> {
    fun upsert(obj: OBJ)
    fun delete(id: ID)
    fun clear()

    abstract class BaseImpl<ID : ValueVariableSnowflakeId, OBJ : DomainObject<ID>> : ProjectionMemory<ID, OBJ> {

        override fun upsert(obj: OBJ) {
            projection[obj.id] = obj
        }

        override fun delete(id: ID) {
            projection.remove(id)
        }

        override fun clear() {
            projection.clear()
        }

        protected val projection = mutableMapOf<ID, OBJ>()
    }
}

