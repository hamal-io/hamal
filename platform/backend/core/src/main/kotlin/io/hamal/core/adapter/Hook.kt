package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.HookCreateRequested
import io.hamal.lib.domain.request.HookUpdateRequested
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.*
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.lib.domain.request.HookCreateRequest
import io.hamal.lib.domain.request.HookUpdateRequest
import org.springframework.stereotype.Component

interface HookCreatePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: HookCreateRequest,
        responseHandler: (HookCreateRequested) -> T
    ): T
}

interface HookGetPort {
    operator fun <T : Any> invoke(hookId: HookId, responseHandler: (Hook, Flow) -> T): T
}

interface HookListPort {
    operator fun <T : Any> invoke(query: HookQuery, responseHandler: (List<Hook>, Map<FlowId, Flow>) -> T): T
}

interface HookUpdatePort {
    operator fun <T : Any> invoke(
        hookId: HookId,
        req: HookUpdateRequest,
        responseHandler: (HookUpdateRequested) -> T
    ): T
}

interface HookPort : HookCreatePort, HookGetPort, HookListPort, HookUpdatePort

@Component
class HookAdapter(
    private val generateDomainId: GenerateId,
    private val hookQueryRepository: HookQueryRepository,
    private val flowQueryRepository: FlowQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : HookPort {
    override fun <T : Any> invoke(
        flowId: FlowId,
        req: HookCreateRequest,
        responseHandler: (HookCreateRequested) -> T
    ): T {
        val flow = flowQueryRepository.get(flowId)
        return HookCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            hookId = generateDomainId(::HookId),
            groupId = flow.groupId,
            flowId = flow.id,
            name = req.name
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(hookId: HookId, responseHandler: (Hook, Flow) -> T): T {
        val hook = hookQueryRepository.get(hookId)
        val flows = flowQueryRepository.get(hook.flowId)
        return responseHandler(hook, flows)
    }


    override fun <T : Any> invoke(
        query: HookQuery,
        responseHandler: (List<Hook>, Map<FlowId, Flow>) -> T
    ): T {
        val hooks = hookQueryRepository.list(query)
        val flows = flowQueryRepository.list(hooks.map(Hook::flowId))
            .associateBy(Flow::id)
        return responseHandler(hooks, flows)
    }

    override fun <T : Any> invoke(
        hookId: HookId,
        req: HookUpdateRequest,
        responseHandler: (HookUpdateRequested) -> T
    ): T {
        ensureHookExists(hookId)
        return HookUpdateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            groupId = hookQueryRepository.get(hookId).groupId,
            hookId = hookId,
            name = req.name,
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    private fun ensureHookExists(hookId: HookId) {
        hookQueryRepository.get(hookId)
    }
}