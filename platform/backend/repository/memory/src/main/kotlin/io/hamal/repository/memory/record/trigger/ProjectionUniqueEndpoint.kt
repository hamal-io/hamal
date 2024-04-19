package io.hamal.repository.memory.record.trigger

import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Trigger
import io.hamal.repository.memory.record.ProjectionMemory


internal class ProjectionUniqueEndpoint : ProjectionMemory<TriggerId, Trigger.Endpoint> {

    override fun upsert(obj: Trigger.Endpoint) {
        if (!uniqueEndpoints.add(
                UniqueEndpoint(
                    funcId = obj.funcId,
                    endpointId = obj.endpointId
                )
            )
        ) {
            throw IllegalArgumentException("Trigger already exists")
        }
    }

    override fun clear() {
        uniqueEndpoints.clear()
    }

    data class UniqueEndpoint(
        val funcId: FuncId,
        val endpointId: EndpointId
    )

    private val uniqueEndpoints = mutableSetOf<UniqueEndpoint>()
}