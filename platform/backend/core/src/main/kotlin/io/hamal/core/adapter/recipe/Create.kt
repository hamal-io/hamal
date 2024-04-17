package io.hamal.core.adapter.recipe

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.RecipeCreateRequest
import io.hamal.lib.domain.request.RecipeCreateRequested
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.RecipeId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface RecipeCreatePort {
    operator fun invoke(accountId: AccountId, req: RecipeCreateRequest): RecipeCreateRequested
}

@Component
class RecipeCreateAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : RecipeCreatePort {
    override fun invoke(accountId: AccountId, req: RecipeCreateRequest): RecipeCreateRequested {
        return RecipeCreateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = generateDomainId(::RecipeId),
            name = req.name,
            inputs = req.inputs,
            value = req.value,
            creatorId = accountId,
            description = req.description
        ).also(requestEnqueue::invoke)
    }

}