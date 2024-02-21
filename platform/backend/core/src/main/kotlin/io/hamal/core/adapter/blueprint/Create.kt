package io.hamal.core.adapter.blueprint

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.BlueprintCreateRequest
import io.hamal.lib.domain.request.BlueprintCreateRequested
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface BlueprintCreatePort {
    operator fun invoke(accountId: AccountId, req: BlueprintCreateRequest): BlueprintCreateRequested
}

@Component
class BlueprintCreateAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : BlueprintCreatePort {
    override fun invoke(accountId: AccountId, req: BlueprintCreateRequest): BlueprintCreateRequested {
        return BlueprintCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            blueprintId = generateDomainId(::BlueprintId),
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            creatorId = accountId,
            description = req.description
        ).also(requestEnqueue::invoke)
    }

}