//package io.hamal.core.adapter.trigger
//
//import io.hamal.core.adapter.request.RequestEnqueuePort
//import io.hamal.core.security.SecurityContext
//import io.hamal.lib.domain.GenerateDomainId
//import io.hamal.lib.domain._enum.RequestStatus
//import io.hamal.lib.domain.request.ExecInvokeRequested
//import io.hamal.lib.domain.vo.InvocationInputs
//import io.hamal.lib.domain.vo.RequestId
//import io.hamal.lib.domain.vo.TriggerId
//import org.springframework.stereotype.Component
//
//fun interface TriggerInvokeHookPort {
//    operator fun invoke(id: TriggerId, inputs: InvocationInputs): ExecInvokeRequested
//}
//
//@Component
//class TriggerInvokeHookAdapter(
//    private val hookGet: HookGetPort,
//    private val generateDomainId: GenerateDomainId,
//    private val requestEnqueue: RequestEnqueuePort
//) : TriggerInvokeHookPort {
//    override fun invoke(id: TriggerId, inputs: InvocationInputs): ExecInvokeRequested {
//        val hookTrigger = hookGet(id)
//        return ExecInvokeRequested(
//            requestId = generateDomainId(::RequestId),
//            requestedBy = SecurityContext.currentAuthId,
//            requestStatus = RequestStatus.Submitted,
//            id = triggerId,
//            workspaceId = hookTrigger.workspaceId,
//            inputs = inputs
//        ).also(requestEnqueue::invoke)
//    }
//}