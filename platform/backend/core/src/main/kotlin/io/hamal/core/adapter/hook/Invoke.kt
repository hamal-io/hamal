package io.hamal.core.adapter.hook

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.HookInvokeRequested
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface HookInvokePort {
    operator fun invoke(hookId: HookId, inputs: InvocationInputs): HookInvokeRequested
}

@Component
class HookInvokeAdapter(
    private val hookGet: HookGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : HookInvokePort {
    override fun invoke(hookId: HookId, inputs: InvocationInputs): HookInvokeRequested {
        val hook = hookGet(hookId)
        return HookInvokeRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = hookId,
            workspaceId = hook.workspaceId,
            inputs = inputs
        ).also(requestEnqueue::invoke)
    }
}