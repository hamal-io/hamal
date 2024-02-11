package io.hamal.core.request.handler.hook

import io.hamal.core.adapter.FuncInvokePort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType.Hook
import io.hamal.lib.domain.request.FuncInvokeRequest
import io.hamal.lib.domain.request.HookInvokeRequested
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.Invocation
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.stereotype.Component


@Component
class HookInvokeHandler(
    private val hookQueryRepository: HookQueryRepository,
    private val invokeFunc: FuncInvokePort,
    private val triggerQueryRepository: TriggerQueryRepository
) : io.hamal.core.request.RequestHandler<HookInvokeRequested>(HookInvokeRequested::class) {

    /**
     * At least once delivery is good enough for now
     */
    override fun invoke(req: HookInvokeRequested) {
        val hook = hookQueryRepository.find(req.hookId) ?: return

        val triggers = triggerQueryRepository.list(
            TriggerQuery(
                types = listOf(Hook),
                limit = Limit.all,
                groupIds = listOf(hook.groupId),
                hookIds = listOf(hook.id)
            )
        ).filterIsInstance<Trigger.Hook>()

        triggers.forEach { trigger ->
            invokeFunc(
                trigger.funcId,
                object : FuncInvokeRequest {
                    override val correlationId = trigger.correlationId ?: CorrelationId.default
                    override val inputs = InvocationInputs()
                    override val invocation: Invocation = req.invocation
                }
            ) {}
        }
    }
}