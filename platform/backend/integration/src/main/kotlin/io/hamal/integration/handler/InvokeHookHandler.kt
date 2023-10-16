package io.hamal.integration.handler

import io.hamal.core.req.InvokeExecReq
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.SubmitRequest
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.repository.api.Func
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
    private val submitRequest: SubmitRequest
) : ReqHandler<SubmittedInvokeHookReq>(SubmittedInvokeHookReq::class) {

    /**
     * At least once delivery is good enough for now
     */
    override fun invoke(req: SubmittedInvokeHookReq) {
        val hook = hookQueryRepository.get(req.id)

        val triggers = triggerQueryRepository.list(
            TriggerQueryRepository.TriggerQuery(
                types = listOf(TriggerType.Hook),
                limit = Limit.all,
                groupIds = listOf(hook.groupId),
                hookIds = listOf(hook.id)
            )
        )

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
                    execId = req.execId,
                    funcId = trigger.funcId,
                    correlationId = trigger.correlationId ?: CorrelationId("__default__"),
                    inputs = InvocationInputs(),
                    code = func.code.toExecCode(),
                    events = listOf()
                )
            )
        }
    }
}