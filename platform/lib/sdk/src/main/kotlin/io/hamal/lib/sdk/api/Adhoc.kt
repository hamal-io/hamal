package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.request.AdhocInvokeReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiAdhocInvokeReq(
    override val inputs: InvocationInputs,
    override val code: CodeValue
) : AdhocInvokeReq


interface ApiAdhocService {
    operator fun invoke(flowId: FlowId, req: ApiAdhocInvokeReq): ApiExecInvokeSubmitted
}

internal class ApiAdhocServiceImpl(
    private val template: HttpTemplate
) : ApiAdhocService {
    override fun invoke(flowId: FlowId, req: ApiAdhocInvokeReq): ApiExecInvokeSubmitted {
        return template
            .post("/v1/flows/{flowId}/adhoc")
            .path("flowId", flowId)
            .body(req)
            .execute(ApiExecInvokeSubmitted::class)
    }
}