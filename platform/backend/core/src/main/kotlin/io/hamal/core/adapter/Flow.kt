package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.FlowCreateRequest
import io.hamal.lib.domain.request.FlowUpdateRequest
import io.hamal.lib.domain.request.FlowCreateRequested
import io.hamal.lib.domain.request.FlowUpdateRequested
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowType
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowQueryRepository
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface FlowCreatePort {
    operator fun <T : Any> invoke(
        groupId: GroupId,
        req: FlowCreateRequest,
        responseHandler: (FlowCreateRequested) -> T
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
        req: FlowUpdateRequest,
        responseHandler: (FlowUpdateRequested) -> T
    ): T
}

interface FlowPort : FlowCreatePort, FlowGetPort, FlowListPort, FlowUpdatePort

@Component
class FlowAdapter(
    private val generateDomainId: GenerateId,
    private val flowQueryRepository: FlowQueryRepository,
    private val requestCmdRepository: RequestCmdRepository
) : FlowPort {

    override fun <T : Any> invoke(
        groupId: GroupId,
        req: FlowCreateRequest,
        responseHandler: (FlowCreateRequested) -> T
    ): T {
        return FlowCreateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            flowId = generateDomainId(::FlowId),
            groupId = groupId,
            flowType = req.type ?: FlowType.default,
            name = req.name,
            inputs = req.inputs
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(flowId: FlowId, responseHandler: (Flow) -> T): T =
        responseHandler(flowQueryRepository.get(flowId))

    override fun <T : Any> invoke(query: FlowQuery, responseHandler: (List<Flow>) -> T): T =
        responseHandler(flowQueryRepository.list(query))


    override operator fun <T : Any> invoke(
        flowId: FlowId,
        req: FlowUpdateRequest,
        responseHandler: (FlowUpdateRequested) -> T
    ): T {
        ensureFlowExists(flowId)
        return FlowUpdateRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            groupId = flowQueryRepository.get(flowId).groupId,
            flowId = flowId,
            name = req.name,
            inputs = req.inputs
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    private fun ensureFlowExists(flowId: FlowId) {
        flowQueryRepository.get(flowId)
    }
}