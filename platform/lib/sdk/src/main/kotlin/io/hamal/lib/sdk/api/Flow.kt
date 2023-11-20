package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateFlowReq
import io.hamal.request.UpdateFlowReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiFlowCreateReq(
    override val name: FlowName,
    override val inputs: FlowInputs,
    override val type: FlowType? = FlowType.default
) : CreateFlowReq

@Serializable
data class ApiFlowCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val flowId: FlowId,
    val groupId: GroupId,
) : ApiSubmitted

@Serializable
data class ApiFlowUpdateReq(
    override val name: FlowName,
    override val inputs: FlowInputs,
) : UpdateFlowReq

@Serializable
data class ApiFlowUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val flowId: FlowId,
) : ApiSubmitted

@Serializable
data class ApiFlowList(
    val flows: List<Flow>
) {
    @Serializable
    data class Flow(
        val type: FlowType,
        val id: FlowId,
        val name: FlowName
    )
}

@Serializable
data class ApiFlow(
    val id: FlowId,
    val type: FlowType,
    val name: FlowName,
    val inputs: FlowInputs
)

interface ApiFlowService {
    fun create(groupId: GroupId, createFlowReq: ApiFlowCreateReq): ApiFlowCreateSubmitted
    fun list(groupId: GroupId): List<ApiFlowList.Flow>
    fun get(flowId: FlowId): ApiFlow
}

internal class ApiFlowServiceImpl(
    private val template: HttpTemplate
) : ApiFlowService {

    override fun create(groupId: GroupId, createFlowReq: ApiFlowCreateReq): ApiFlowCreateSubmitted =
        template.post("/v1/groups/{groupId}/flows")
            .path("groupId", groupId)
            .body(createFlowReq)
            .execute()
            .fold(ApiFlowCreateSubmitted::class)

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