package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.code.CodeGetPort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.request.TriggerInvokeRequest
import io.hamal.lib.domain.vo.*
import org.springframework.stereotype.Component

fun interface TriggerInvokePort {
    operator fun invoke(triggerId: TriggerId, req: TriggerInvokeRequest, invocation: Invocation): ExecInvokeRequested
}

@Component
class TriggerInvokeAdapter(
    private val triggerGet: TriggerGetPort,
    private val funcGet: FuncGetPort,
    private val codeGet: CodeGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : TriggerInvokePort {

    override fun invoke(
        triggerId: TriggerId, req: TriggerInvokeRequest, invocation: Invocation
    ): ExecInvokeRequested {

        val trigger = triggerGet(triggerId)
        val func = funcGet(trigger.funcId)
        val code = codeGet(func.deployment.id, func.deployment.version)

        return ExecInvokeRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = generateDomainId(::ExecId),
            triggerId = triggerId,
            namespaceId = func.namespaceId,
            workspaceId = func.workspaceId,
            funcId = func.id,
            correlationId = req.correlationId,
            inputs = req.inputs ?: InvocationInputs(),
            code = ExecCode(
                id = code.id, version = code.version, value = code.value
            ),
            invocation = invocation
        ).also(requestEnqueue::invoke)
    }
}
