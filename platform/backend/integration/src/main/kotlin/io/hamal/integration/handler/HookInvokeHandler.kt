package io.hamal.integration.handler

import io.hamal.core.adapter.FuncInvokePort
import io.hamal.core.req.ReqHandler
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.TriggerType.Hook
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.repository.api.*
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import io.hamal.repository.api.submitted_req.HookInvokeSubmitted
import io.hamal.request.InvokeFuncReq
import org.springframework.stereotype.Component

@Component
class HookInvokeHandler(
    private val funcQueryRepository: FuncQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val hookQueryRepository: HookQueryRepository,
    private val invokeFunc: FuncInvokePort,
    private val triggerQueryRepository: TriggerQueryRepository
) : ReqHandler<HookInvokeSubmitted>(HookInvokeSubmitted::class) {

    /**
     * At least once delivery is good enough for now
     */
    override fun invoke(req: HookInvokeSubmitted) {
        val hook = hookQueryRepository.find(req.hookId) ?: return

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
            invokeFunc(
                trigger.funcId,
                object : InvokeFuncReq {
                    override val correlationId = trigger.correlationId ?: CorrelationId.default
                    override val inputs = InvocationInputs()
                    override val events = listOf<Event>()
                    override val version = null
                }
            ) {}
        }
    }
}