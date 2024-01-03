package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiEndpointService.EndpointQuery
import io.hamal.lib.sdk.fold
import io.hamal.lib.domain.request.EndpointCreateRequest
import io.hamal.lib.domain.request.EndpointUpdateRequest

data class ApiEndpointCreateRequest(
    override val funcId: FuncId,
    override val name: EndpointName,
    override val method: EndpointMethod
) : EndpointCreateRequest

data class ApiEndpointCreateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val endpointId: EndpointId,
    val groupId: GroupId,
    val funcId: FuncId
) : ApiRequested


data class ApiEndpointUpdateRequest(
    override val funcId: FuncId? = null,
    override val name: EndpointName? = null,
    override val method: EndpointMethod? = null
) : EndpointUpdateRequest

data class ApiEndpointUpdateRequested(
    override val id: RequestId,
    override val status: RequestStatus,
    val endpointId: EndpointId,
) : ApiRequested

data class ApiEndpointList(
    val endpoints: List<Endpoint>
) {
    data class Endpoint(
        val id: EndpointId,
        val func: Func,
        val name: EndpointName
    ) {
        data class Func(
            val id: FuncId,
            val name: FuncName
        )
    }
}

data class ApiEndpoint(
    val id: EndpointId,
    val func: Func,
    val name: EndpointName
) {
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

interface ApiEndpointService {
    fun create(flowId: FlowId, createEndpointReq: ApiEndpointCreateRequest): ApiEndpointCreateRequested
    fun list(query: EndpointQuery): List<ApiEndpointList.Endpoint>
    fun get(endpointId: EndpointId): ApiEndpoint

    data class EndpointQuery(
        var afterId: EndpointId = EndpointId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(25),
        var endpointIds: List<EndpointId> = listOf(),
        var flowIds: List<FlowId> = listOf(),
        var groupIds: List<GroupId> = listOf()
    ) {
        fun setRequestParameters(req: HttpRequest) {
            req.parameter("after_id", afterId)
            req.parameter("limit", limit)
            if (endpointIds.isNotEmpty()) req.parameter("endpoint_ids", endpointIds)
            if (flowIds.isNotEmpty()) req.parameter("flow_ids", flowIds)
            if (groupIds.isNotEmpty()) req.parameter("group_ids", groupIds)
        }
    }
}

internal class ApiEndpointServiceImpl(
    private val template: HttpTemplate
) : ApiEndpointService {

    override fun create(flowId: FlowId, createEndpointReq: ApiEndpointCreateRequest) =
        template.post("/v1/flows/{flowId}/endpoints")
            .path("flowId", flowId)
            .body(createEndpointReq)
            .execute(ApiEndpointCreateRequested::class)

    override fun list(query: EndpointQuery): List<ApiEndpointList.Endpoint> =
        template.get("/v1/endpoints")
            .also(query::setRequestParameters)
            .execute()
            .fold(ApiEndpointList::class)
            .endpoints

    override fun get(endpointId: EndpointId) =
        template.get("/v1/endpoints/{endpointId}")
            .path("endpointId", endpointId)
            .execute()
            .fold(ApiEndpoint::class)
}