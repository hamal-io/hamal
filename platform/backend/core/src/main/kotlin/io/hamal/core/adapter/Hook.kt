package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.*
import io.hamal.repository.api.HookQueryRepository.HookQuery
import io.hamal.repository.api.submitted_req.HookCreateSubmitted
import io.hamal.repository.api.submitted_req.HookUpdateSubmitted
import io.hamal.request.HookCreateReq
import io.hamal.request.HookUpdateReq
import org.springframework.stereotype.Component

interface HookCreatePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: HookCreateReq,
        responseHandler: (HookCreateSubmitted) -> T
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
        req: HookUpdateReq,
        responseHandler: (HookUpdateSubmitted) -> T
    ): T
}

interface HookPort : HookCreatePort, HookGetPort, HookListPort, HookUpdatePort

@Component
class HookAdapter(
    private val generateDomainId: GenerateDomainId,
    private val hookQueryRepository: HookQueryRepository,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) : HookPort {
    override fun <T : Any> invoke(
        flowId: FlowId,
        req: HookCreateReq,
        responseHandler: (HookCreateSubmitted) -> T
    ): T {
        val flow = flowQueryRepository.get(flowId)
        return HookCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            hookId = generateDomainId(::HookId),
            groupId = flow.groupId,
            flowId = flow.id,
            name = req.name
        ).also(reqCmdRepository::queue).let(responseHandler)
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
        req: HookUpdateReq,
        responseHandler: (HookUpdateSubmitted) -> T
    ): T {
        ensureHookExists(hookId)
        return HookUpdateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            groupId = hookQueryRepository.get(hookId).groupId,
            hookId = hookId,
            name = req.name,
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    private fun ensureHookExists(hookId: HookId) {
        hookQueryRepository.get(hookId)
    }
}