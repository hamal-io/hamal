package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.FlowCreateSubmitted
import io.hamal.repository.api.submitted_req.FlowUpdateSubmitted
import io.hamal.request.CreateFlowReq
import io.hamal.request.UpdateFlowReq
import org.springframework.stereotype.Component

interface FlowCreatePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateFlowReq,
        responseHandler: (FlowCreateSubmitted) -> T
    ): T
}

interface FlowGetPort {
    operator fun <T : Any> invoke(flowId: FlowId, responseHandler: (Flow) -> T): T
}

interface FlowListPort {
    operator fun <T : Any> invoke(query: FlowQuery, responseHandler: (List<Flow>) -> T): T
}


interface FlowUpdatePort {
    operator fun <T : Any> invoke(
        flowId: FlowId,
        req: UpdateFlowReq,
        responseHandler: (FlowUpdateSubmitted) -> T
    ): T
}

interface FlowPort : FlowCreatePort, FlowGetPort, FlowListPort, FlowUpdatePort

@Component
class FlowAdapter(
    private val generateDomainId: GenerateDomainId,
    private val flowQueryRepository: FlowQueryRepository,
    private val reqCmdRepository: ReqCmdRepository
) : FlowPort {

    override fun <T : Any> invoke(
        groupId: GroupId,
        req: CreateFlowReq,
        responseHandler: (FlowCreateSubmitted) -> T
    ): T {
        return FlowCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            flowId = generateDomainId(::FlowId),
            groupId = groupId,
            type = req.type,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(flowId: FlowId, responseHandler: (Flow) -> T): T =
        responseHandler(flowQueryRepository.get(flowId))

    override fun <T : Any> invoke(query: FlowQuery, responseHandler: (List<Flow>) -> T): T =
        responseHandler(flowQueryRepository.list(query))


    override operator fun <T : Any> invoke(
        flowId: FlowId,
        req: UpdateFlowReq,
        responseHandler: (FlowUpdateSubmitted) -> T
    ): T {
        ensureFlowExists(flowId)
        return FlowUpdateSubmitted(
            id = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            groupId = flowQueryRepository.get(flowId).groupId,
            flowId = flowId,
            name = req.name,
            inputs = req.inputs
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    private fun ensureFlowExists(flowId: FlowId) {
        flowQueryRepository.get(flowId)
    }
}