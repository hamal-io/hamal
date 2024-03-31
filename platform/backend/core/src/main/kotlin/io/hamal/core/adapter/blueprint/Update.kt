package io.hamal.core.adapter.blueprint

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.BlueprintUpdateRequest
import io.hamal.lib.domain.request.BlueprintUpdateRequested
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface BlueprintUpdatePort {
    operator fun invoke(blueprintId: BlueprintId, req: BlueprintUpdateRequest): BlueprintUpdateRequested
}

@Component
class BlueprintUpdateAdapter(
    private val blueprintGet: BlueprintGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : BlueprintUpdatePort {
    override fun invoke(blueprintId: BlueprintId, req: BlueprintUpdateRequest): BlueprintUpdateRequested {
        ensureBlueprintExists(blueprintId)
        return BlueprintUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = blueprintId,
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            description = req.description
        ).also(requestEnqueue::invoke)
    }

    private fun ensureBlueprintExists(blueprintId: BlueprintId) = blueprintGet(blueprintId)
}