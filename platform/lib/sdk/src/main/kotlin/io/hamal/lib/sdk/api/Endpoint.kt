package io.hamal.lib.sdk.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpRequest
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.sdk.api.ApiEndpointService.EndpointQuery
import io.hamal.lib.sdk.fold
import io.hamal.request.CreateEndpointReq
import io.hamal.request.UpdateEndpointReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiEndpointCreateReq(
    override val funcId: FuncId,
    override val name: EndpointName,
    override val method: EndpointMethod
) : CreateEndpointReq

@Serializable
data class ApiEndpointCreateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val endpointId: EndpointId,
    val groupId: GroupId,
    val funcId: FuncId
) : ApiSubmitted


@Serializable
data class ApiUpdateEndpointReq(
    override val funcId: FuncId? = null,
    override val name: EndpointName? = null,
    override val method: EndpointMethod? = null
) : UpdateEndpointReq

@Serializable
data class ApiEndpointUpdateSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val endpointId: EndpointId,
) : ApiSubmitted

@Serializable
data class ApiEndpointList(
    val endpoints: List<Endpoint>
) {
    @Serializable
    data class Endpoint(
        val id: EndpointId,
        val func: Func,
        val name: EndpointName,
        val method: EndpointMethod
    ) {
        @Serializable
        data class Func(
            val id: FuncId,
            val name: FuncName
        )
    }
}

@Serializable
data class ApiEndpoint(
    val id: EndpointId,
    val func: Func,
    val name: EndpointName,
    val method: EndpointMethod
) {
    @Serializable
    data class Func(
        val id: FuncId,
        val name: FuncName
    )
}

interface ApiEndpointService {
    fun create(flowId: FlowId, createEndpointReq: ApiEndpointCreateReq): ApiEndpointCreateSubmitted
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

    override fun create(flowId: FlowId, createEndpointReq: ApiEndpointCreateReq) =
        template.post("/v1/flows/{flowId}/endpoints")
            .path("flowId", flowId)
            .body(createEndpointReq)
            .execute(ApiEndpointCreateSubmitted::class)

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