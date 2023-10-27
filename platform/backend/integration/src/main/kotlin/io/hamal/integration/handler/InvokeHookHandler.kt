package io.hamal.integration.handler

import io.hamal.core.req.InvokeExecReq
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.SubmitRequest
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.TriggerType.Hook
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.submitted_req.SubmittedInvokeHookReq
import org.springframework.stereotype.Component

@Component
class InvokeHookHandler(
    private val hookQueryRepository: HookQueryRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val submitRequest: SubmitRequest,
    private val generateDomainId: GenerateDomainId
) : ReqHandler<SubmittedInvokeHookReq>(SubmittedInvokeHookReq::class) {

    /**
     * At least once delivery is good enough for now
     */
    override fun invoke(req: SubmittedInvokeHookReq) {
        val hook = hookQueryRepository.find(req.id) ?: return

        val triggers = triggerQueryRepository.list(
            TriggerQuery(
                types = listOf(Hook),
                limit = Limit.all,
                groupIds = listOf(hook.groupId),
                hookIds = listOf(hook.id)
            )
        )
            .filterIsInstance<HookTrigger>()
            .filter { it.hookMethods.contains(req.method) }

        val funcs = funcQueryRepository.list(
            FuncQueryRepository.FuncQuery(
                groupIds = listOf(hook.groupId),
                funcIds = triggers.map { it.funcId }
            )
        ).associateBy(Func::id)

        triggers.forEach { trigger ->
            val func = funcs[trigger.funcId]!!

            submitRequest(
                InvokeExecReq(
                    execId = generateDomainId(::ExecId),
                    funcId = trigger.funcId,
                    correlationId = trigger.correlationId ?: CorrelationId.default,
                    inputs = InvocationInputs(),
                    code = func.code.toExecCode(),
                    events = listOf()
                )
            )
        }
    }
}