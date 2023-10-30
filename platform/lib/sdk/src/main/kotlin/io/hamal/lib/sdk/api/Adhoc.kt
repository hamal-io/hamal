package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.request.InvokeAdhocReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiAdhocInvokeReq(
    override val inputs: InvocationInputs,
    override val code: CodeValue
) : InvokeAdhocReq


interface ApiAdhocService {
    operator fun invoke(namespaceId: NamespaceId, req: ApiAdhocInvokeReq): ApiExecInvokeSubmitted
}

internal class ApiAdhocServiceImpl(
    private val template: HttpTemplate
) : ApiAdhocService {
    override fun invoke(namespaceId: NamespaceId, req: ApiAdhocInvokeReq): ApiExecInvokeSubmitted {
        return template
            .post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute(ApiExecInvokeSubmitted::class)
    }
}