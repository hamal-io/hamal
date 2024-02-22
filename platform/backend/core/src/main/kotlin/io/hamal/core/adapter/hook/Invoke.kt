package io.hamal.core.adapter.hook

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.HookInvokeRequested
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.Invocation
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface HookInvokePort {
    operator fun invoke(hookId: HookId, invocation: Invocation.Hook): HookInvokeRequested
}

@Component
class HookInvokeAdapter(
    private val hookGet: HookGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : HookInvokePort {
    override fun invoke(hookId: HookId, invocation: Invocation.Hook): HookInvokeRequested {
        val hook = hookGet(hookId)
        return HookInvokeRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            hookId = hookId,
            workspaceId = hook.workspaceId,
            invocation = invocation,
        ).also(requestEnqueue::invoke)
    }
}