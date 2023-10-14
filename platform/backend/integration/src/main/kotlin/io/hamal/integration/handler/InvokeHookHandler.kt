package io.hamal.integration.handler

import io.hamal.core.req.InvokeExecReq
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.SubmitRequest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedInvokeHookReq
import org.springframework.stereotype.Component

@Component
class InvokeHookHandler(
    private val hookQueryRepository: HookQueryRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val submitRequest: SubmitRequest,
    private val generateDomainId: GenerateDomainId,
) : ReqHandler<SubmittedInvokeHookReq>(SubmittedInvokeHookReq::class) {
    override fun invoke(req: SubmittedInvokeHookReq) {
        val hook = hookQueryRepository.get(req.id)
        val triggers = triggerQueryRepository.list(
            TriggerQueryRepository.TriggerQuery(
                types = listOf(TriggerType.Hook),
                limit = Limit.all,
                groupIds = listOf(hook.groupId)
            )
        )
        // resolve triggers via hook
        // get funcs from trigger
        // trigger exec

        val trigger = EventTrigger(
            cmdId = CmdId(1),
            id = TriggerId(1),
            groupId = GroupId(1),
            funcId = FuncId("13a342ac01000"),
            name = TriggerName("abc"),
            correlationId = null,
            inputs = TriggerInputs(),
            namespaceId = NamespaceId(1),
            topicId = TopicId(1)
        )

        println(hook)

        val func = funcQueryRepository.get(trigger.funcId)

        submitRequest(
            InvokeExecReq(
                execId = generateDomainId(::ExecId),
                funcId = trigger.funcId,
                correlationId = trigger.correlationId ?: CorrelationId("__default__"),
                inputs = InvocationInputs(),
                code = null,
                codeId = func.codeId,
                codeVersion = func.codeVersion,
                events = listOf()
            )
        )
    }
}