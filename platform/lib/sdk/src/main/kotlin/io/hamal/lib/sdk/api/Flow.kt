package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.lib.domain.request.FlowCreateRequest
import io.hamal.lib.domain.request.FlowUpdateRequest

data class ApiFlowCreateRequest(
    override val name: FlowName,
    override val inputs: FlowInputs,
    override val type: FlowType? = null
) : FlowCreateRequest

data class ApiFlowCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val flowId: FlowId,
    val groupId: GroupId,
) : ApiRequested

data class ApiFlowUpdateRequest(
    override val name: FlowName,
    override val inputs: FlowInputs,
) : FlowUpdateRequest

data class ApiFlowUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val flowId: FlowId,
) : ApiRequested

data class ApiFlowList(
    val flows: List<Flow>
) {
    data class Flow(
        val type: FlowType,
        val id: FlowId,
        val name: FlowName
    )
}

data class ApiFlow(
    val id: FlowId,
    val type: FlowType,
    val name: FlowName,
    val inputs: FlowInputs
)

interface ApiFlowService {
    fun create(groupId: GroupId, createFlowReq: ApiFlowCreateRequest): ApiFlowCreateRequested
    fun list(groupId: GroupId): List<ApiFlowList.Flow>
    fun get(flowId: FlowId): ApiFlow
}

internal class ApiFlowServiceImpl(
    private val template: HttpTemplate
) : ApiFlowService {

    override fun create(groupId: GroupId, createFlowReq: ApiFlowCreateRequest): ApiFlowCreateRequested =
        template.post("/v1/groups/{groupId}/flows")
            .path("groupId", groupId)
            .body(createFlowReq)
            .execute()
            .fold(ApiFlowCreateRequested::class)

    override fun list(groupId: GroupId) =
        template.get("/v1/groups/{groupId}/flows")
            .path("groupId", groupId)
            .execute()
            .fold(ApiFlowList::class)
            .flows

    override fun get(flowId: FlowId) =
        template.get("/v1/flows/{flowId}")
            .path("flowId", flowId)
            .execute()
            .fold(ApiFlow::class)
}