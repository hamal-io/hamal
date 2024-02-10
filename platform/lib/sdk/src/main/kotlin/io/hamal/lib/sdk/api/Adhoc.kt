package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.domain.request.AdhocInvokeRequest

data class ApiAdhocInvokeRequest(
    override val inputs: InvocationInputs,
    override val code: CodeValue
) : AdhocInvokeRequest


interface ApiAdhocService {
    operator fun invoke(namespaceId: NamespaceId, req: ApiAdhocInvokeRequest): ApiExecInvokeRequested
}

internal class ApiAdhocServiceImpl(
    private val template: HttpTemplate
) : ApiAdhocService {
    override fun invoke(namespaceId: NamespaceId, req: ApiAdhocInvokeRequest): ApiExecInvokeRequested {
        return template
            .post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", namespaceId)
            .body(req)
            .execute(ApiExecInvokeRequested::class)
    }
}