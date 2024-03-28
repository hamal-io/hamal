package io.hamal.core.request.handler.hook

import io.hamal.core.adapter.trigger.TriggerInvokePort
import io.hamal.core.request.RequestHandler
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType.Hook
import io.hamal.lib.domain.request.HookInvokeRequested
import io.hamal.lib.domain.request.TriggerInvokeRequest
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.stereotype.Component


@Component
class HookInvokeHandler(
    private val hookQueryRepository: HookQueryRepository,
    private val triggerInvoke: TriggerInvokePort,
    private val triggerQueryRepository: TriggerQueryRepository
) : RequestHandler<HookInvokeRequested>(HookInvokeRequested::class) {

    /**
     * At least once delivery is good enough for now
     */
    override fun invoke(req: HookInvokeRequested) {
        val hook = hookQueryRepository.find(req.id) ?: return

        val triggers = triggerQueryRepository.list(
            TriggerQuery(
                types = listOf(Hook),
                limit = Limit.all,
                workspaceIds = listOf(hook.workspaceId),
                hookIds = listOf(hook.id)
            )
        ).filterIsInstance<Trigger.Hook>()

        triggers.forEach { trigger ->
            triggerInvoke(
                trigger.id,
                object : TriggerInvokeRequest {
                    override val correlationId = trigger.correlationId ?: CorrelationId.default
                    override val inputs = InvocationInputs()
                },
                req.invocation
            )
        }
    }
}