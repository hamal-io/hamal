package io.hamal.core.adapter.hook

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.HookUpdateRequest
import io.hamal.lib.domain.request.HookUpdateRequested
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface HookUpdatePort {
    operator fun invoke(hookId: HookId, req: HookUpdateRequest): HookUpdateRequested
}

@Component
class HookUpdateAdapter(
    private val hookGet: HookGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : HookUpdatePort {
    override fun invoke(hookId: HookId, req: HookUpdateRequest): HookUpdateRequested {
        val hook = hookGet(hookId)
        return HookUpdateRequested(
            id = generateDomainId(::RequestId),
            by = SecurityContext.currentAuthId,
            status = RequestStatus.Submitted,
            workspaceId = hook.workspaceId,
            hookId = hookId,
            name = req.name,
        ).also(requestEnqueue::invoke)
    }
}