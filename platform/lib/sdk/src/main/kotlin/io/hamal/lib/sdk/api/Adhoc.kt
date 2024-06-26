package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.CodeTypes
import io.hamal.lib.domain.request.AdhocInvokeRequest
import io.hamal.lib.domain.vo.CodeType
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body

data class ApiAdhocInvokeRequest(
    override val inputs: InvocationInputs? = null,
    override val code: CodeValue,
    override val codeType: CodeType = CodeType(CodeTypes.Lua54)
) : AdhocInvokeRequest


interface ApiAdhocService {
    operator fun invoke(namespaceId: NamespaceId, request: ApiAdhocInvokeRequest): ApiExecInvokeRequested
}

internal class ApiAdhocServiceImpl(
    private val template: HttpTemplate
) : ApiAdhocService {
    override fun invoke(namespaceId: NamespaceId, request: ApiAdhocInvokeRequest): ApiExecInvokeRequested {
        return template
            .post("/v1/namespaces/{namespaceId}/adhoc")
            .path("namespaceId", namespaceId)
            .body(request)
            .execute(ApiExecInvokeRequested::class)
    }
}